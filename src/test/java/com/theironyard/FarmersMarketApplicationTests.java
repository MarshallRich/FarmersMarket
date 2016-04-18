package com.theironyard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theironyard.entities.Category;
import com.theironyard.entities.Inventory;
import com.theironyard.entities.Order;
import com.theironyard.entities.User;
import com.theironyard.services.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FarmersMarketApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FarmersMarketApplicationTests {

    @Autowired
    UserRepository users;

    @Autowired
    InventoryRepository inventories;

    @Autowired
    CategoryRepository categories;

    @Autowired
    OrderRepository orders;

    @Autowired
    WebApplicationContext wap;

    MockMvc mockMvc;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
    }

    @Test
    public void test1CreateUser() throws Exception { //(POST route: /users)
        User user = new User("Alice", "password", "password", "Farmer", "Limehouse Produce", "charleston", "8888", "alice@alice");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(json)
                        .contentType("application/json")
        );
        Assert.assertTrue(users.count() == 5);
    }

    @Test
    public void test2GetOneUser() throws Exception { //(GET route: /users/{id})
    User user = users.findByUserName("Alice");
    Assert.assertTrue(user.getUserName().equals("Alice"));
    }

//    @Test
//    public void test3GetValidatingUsers() throws Exception { //(GET route: /users/validate)
//        ResultActions resAct =
//            mockMvc.perform(
//                    MockMvcRequestBuilders.get("/users/validate")
//                            .sessionAttr("userName", "Admin")
//            );
//        MvcResult result = resAct.andReturn();
//        String returnString = result.getResponse().getContentAsString();
//
//        Scanner fileScanner = new Scanner(returnString);
//        while (fileScanner.hasNext()) {
//            String[] line = returnString.split(",");
//
//            for(String stringLine : line) {
//                String[] column = stringLine.split(": ");
//            }
//            Category c = new Category(column[0], column[1]);
////        Assert.assertTrue(users.findByUserName("Alice").getValid().equals(false));
//        }
//    }

    @Test
    public void test4ValidateUser() throws Exception { //(POST route: /users/validate/{id})
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users/validate/5")
                        .sessionAttr("userName", "Admin")
        );
        Assert.assertTrue(users.findByUserName("Alice").getValid());
    }

//    -getUsersInCategory (GET route: /users/category/{category})

    @Test
    public void test5UpdateUser() throws Exception { //(PUT route: /users/{id})
        User user = users.findOne(5);
        user.setCompanyName("Rawl Produce");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/users/5")
                        .content(json)
                        .contentType("application/json")
                        .sessionAttr("userName", "Alice")
        );
        Assert.assertTrue(users.findOne(5).getCompanyName().equals("Rawl Produce"));
    }

    @Test
    public void test6Login() throws Exception { //(POST route: /login)
        User user = new User();
        user.setUserName("Alice");
        user.setPasswordHash("password");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);
        ResultActions resAct =
                mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .content(json)
                        .contentType("application/json")
        );
        MvcResult result = resAct.andReturn();
        MockHttpServletRequest request = result.getRequest();
        HttpSession session = request.getSession();
        Assert.assertTrue(session.getAttribute("userName").equals("Alice"));
    }

    @Test
    public void test7CreateInventoryUser() throws Exception { //(POST route: /inventory)
        Inventory inventory = new Inventory();
        inventory.setCategory("Banana");
        inventory.setName("Golden Yellow Bananas");
        inventory.setQuantityAvailable(9);
        inventory.setPrice(2.85);
        inventory.setUser(users.findByUserName("Alice"));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(inventory);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/inventory")
                        .content(json)
                        .contentType("application/json")
                        .sessionAttr("userName", "Alice")
        );
        Assert.assertTrue(inventories.count() == 2);
    }

    @Test
    public void test8UpdateInventoryUser() throws Exception { //(PUT route: /inventory/{id})
        Inventory i = inventories.findOne(2);
        i.setCategory("Tomato");
        System.out.println();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(i);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/inventory/2")
                        .content(json)
                        .contentType("application/json")
                        .sessionAttr("userName", "Alice")
        );
        Assert.assertTrue(inventories.findOne(2).getCategory().equals("Tomato"));
    }

    @Test
    public void test9CreateInventoryAdmin() throws Exception { //(POST route: /inventory/user/{id})
        Inventory inventory = new Inventory();
        inventory.setCategory("Banana");
        inventory.setName("Chiquitta Bananas");
        inventory.setQuantityAvailable(11);
        inventory.setPrice(3.55);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(inventory);
        mockMvc.perform(
            MockMvcRequestBuilders.post("/inventory/user/5")
                    .content(json)
                    .contentType("application/json")
                    .sessionAttr("userName", "Admin")
        );
        Assert.assertTrue(inventories.count() == 3);
    }

    @Test
    public void testB1UpdateInventoryAdmin() throws Exception { //(PUT route: /inventory/{id})
        Inventory i = inventories.findOne(2);
        i.setCategory("Corn");
        System.out.println();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(i);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/inventory/2")
                        .content(json)
                        .contentType("application/json")
                        .sessionAttr("userName", "Admin")
        );
        Assert.assertTrue(inventories.findOne(2).getCategory().equals("Corn"));
    }

//    @Test
//    public void testB2FindInventoryByCategory() throws Exception { //(GET route: /inventory/category/{category})
////        ObjectMapper mapper = new ObjectMapper();
////        String json = mapper.writeValueAsString(inventories.findAll());
////        mockMvc.perform(
////                MockMvcRequestBuilders.get("/inventory/category/Tomato")
////                        .content(json)
////                        .contentType("application/json")
////                        .sessionAttr("userName", "Alice")
////        );
//        Assert.assertTrue(inventories.findByCategory("Tomato").size() == 1); //does this test actually work?
//        // is it checking the amount of inventory items in that category?
//        //this test is a placeholder that I'm going to work on redoing soon
//    }

//    -getAllCategories (GET route: /categories)


//    -getCategoriesByLetter (GET route: /categories/{letter})


    @Test
    public void testB3CreateOrderUser() throws Exception { //(POST route: /orders/{invId})
        User user = new User("Bob", "password", "password", "Buyer", "Bob Store", "charleston", "8888", "bob@bob");
        user.setValid(true);
        users.save(user);

        Order order = new Order();
        order.setCategory("Banana");
        order.setName("Golden Yellow Bananas");
        order.setQuantityOrdered(2);
        order.setPrice(2.85);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/orders/2")
                        .content(json)
                        .contentType("application/json")
                        .sessionAttr("userName", "Bob")
        );
        Assert.assertTrue(orders.count() == 1);
    }

    @Test
    public void testB4CreateOrderAdmin() throws Exception { //(POST route: /orders/admin/{buyerId}/{invId})
        Order order = new Order();
        order.setCategory("Banana");
        order.setName("Golden Yellow Bananas");
        order.setQuantityOrdered(3);
        order.setPrice(2.85);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/orders/admin/6/2")
                        .content(json)
                        .contentType("application/json")
                        .sessionAttr("userName", "Admin")
        );
        Assert.assertTrue(orders.count() == 2);
    }

//    -getOrdersPending(GET route: /orders/{pending})


    @Test
    public void testB5DeletePendingOrderFarmerOrBuyerOrAdmin() throws Exception { //(DELETE route: /orders/{id})
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/orders/1")
                .sessionAttr("userName", "Alice")
        );
        Assert.assertTrue(orders.count() == 1);
    }

    @Test
    public void testB6authorizeOrderByFarmerOrAdmin() throws Exception { //(PUT route: /orders/authorize/{id})

        mockMvc.perform(
                MockMvcRequestBuilders.put("/orders/authorize/2")
                        .sessionAttr("userName", "Alice")
        );
        Assert.assertTrue(!orders.findOne(2).isPendingApproval());
    }



    @Test
    public void testB7DeleteInventoryUser() throws Exception { //(DELETE route: /inventory/{id})
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/inventory/2")
                        .sessionAttr("userName", "Alice")
        );
        Assert.assertTrue(inventories.findByUser(users.findByUserName("Alice")).size() == 1 && orders.count() == 1);
    }

    @Test
    public void testB8DeleteInventoryAdmin() throws Exception { //(DELETE route: /inventory/{id})
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/inventory/3")
                        .sessionAttr("userName", "Admin")
        );
        Assert.assertTrue(inventories.findByUser(users.findByUserName("Alice")).size() == 0);
    }

//    -getOrderHistoryForFarmer *
//    -getOderHistoryForBuyer *
//    -logout (POST route: /logout)

}
