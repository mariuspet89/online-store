package eu.accesa.onlinestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.OrderDtoNoId;
import eu.accesa.onlinestore.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static eu.accesa.onlinestore.utils.OrderTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {OrderServiceImpl.class, OrderController.class})
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderServiceImpl orderService;
    @Captor
    private ArgumentCaptor<OrderDtoNoId> orderDtoArgumentCaptor;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void findById() throws Exception {
        String orderId = "orderId";
        OrderDto foundOrder = testOrderDto("orderId", 12.1, "userID");
        when(orderService.findById(foundOrder.getId())).thenReturn(foundOrder);
        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(foundOrder.getId()))
                .andExpect(jsonPath("$.orderValue").value(foundOrder.getOrderValue()))
                .andExpect(jsonPath("$.userId").value(foundOrder.getUserId()));
        verify(orderService).findById(foundOrder.getId());
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void findByUserId() throws Exception {
        String userId = "userId1";
        List<OrderDto> orderByUserId = testOrderDtoList();
        when(orderService.findByUser(userId)).thenReturn(orderByUserId);
        mockMvc.perform(get("/orders/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder("orderId1", "orderId2")))
                .andExpect(jsonPath("$[*].orderValue").value(containsInAnyOrder(1.1, 2.2)))
                .andExpect(jsonPath("$[*].userId").isNotEmpty())
                .andExpect(jsonPath("$[*].userId").value(containsInAnyOrder("userId1", "userId2")));
        verify(orderService).findByUser(userId);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void findAll() throws Exception {
        List<OrderDto> orders = testOrderDtoList();
        when(orderService.findAll()).thenReturn(orders);
        mockMvc.perform(get("/orders/getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder("orderId1", "orderId2")))
                .andExpect(jsonPath("$[*].orderValue").value(containsInAnyOrder(1.1, 2.2)))
                .andExpect(jsonPath("$[*].userId").isNotEmpty())
                .andExpect(jsonPath("$[*].userId").value(containsInAnyOrder("userId1", "userId2")));
        verify(orderService).findAll();
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Disabled
    public void createOrder() throws Exception {
        //GIVEN
        Map<String, Integer> orderedProducts = testHMOrderedProduct("a", 1);
        OrderDtoNoId orderToBeSavedNoId = testOrderDtoNoId(1.1, "orderUserId");

        orderToBeSavedNoId.setOrderedProducts(orderedProducts);
        OrderDto orderToBeSaved = testOrderDto("orderId", 1.1, "orderUserId");
        orderToBeSaved.setOrderedProducts(orderedProducts);

        //WHEN
        doReturn(orderToBeSaved).when(orderService).createOrder(any(OrderDtoNoId.class));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(orderToBeSavedNoId)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        //THEN
        verify(orderService).createOrder(any(OrderDtoNoId.class));
        verify(orderService).createOrder(orderDtoArgumentCaptor.capture());
        assertThat(orderDtoArgumentCaptor.getValue().getOrderValue()).isEqualTo(1.1);
        assertThat(orderDtoArgumentCaptor.getValue().getUserId()).isEqualTo("orderUserId");
        verifyNoMoreInteractions(orderService);
    }

    @Test
    @Disabled
    public void updateOrder() throws Exception {
        String id = "orderId1";
        OrderDtoNoId order = testOrderDtoNoId(1.1, "userId1");
        OrderDto orderToPut = testOrderDto("orderId1", 2.1, "userId1");

        when(orderService.findById(id)).thenReturn(orderToPut);
        when(orderService.updateOrder(id, order)).thenReturn(orderToPut);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String orderToPutJson = objectMapper.writeValueAsString(orderToPut);
        final ResultActions resultActions = mockMvc.perform(put("/orders/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderToPutJson));
        resultActions.andExpect(status().isOk());
        verify(orderService).updateOrder(Mockito.anyString(), orderDtoArgumentCaptor.capture());
        assertThat(orderDtoArgumentCaptor.getValue().getOrderValue()).isEqualTo(2.1);
        assertThat(orderDtoArgumentCaptor.getValue().getUserId()).isEqualTo("userId1");
        verifyNoMoreInteractions(orderService);
    }

    private String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
