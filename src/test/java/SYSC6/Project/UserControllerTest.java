package SYSC6.Project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(2)
    void getUserById() throws Exception {
        this.mockMvc.perform(get("/rest/api/user/"+1)).andExpect(status().isOk()).andExpect(content().json("{\"id\":1,\"username\":\"Nick\",\"password\":\"Help\",\"role\":\"FREE_USER\",\"clicks\":\""+0+"\"}"));
    }

    @Test
    @Order(1)
    void getAllUsers() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("username", "Nick");
        obj.put("password","Help");
        this.mockMvc.perform(post("/rest/api/user/add").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(obj)));
        obj.put("username", "Nazifa");
        obj.put("password","Friend");
        this.mockMvc.perform(post("/rest/api/user/add").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(obj)));
        this.mockMvc.perform(get("/rest/api/user")).andExpect(status().isOk()).andExpect(content().json("[{\"id\":1,\"username\":\"Nick\",\"password\":\"Help\",\"role\":\"FREE_USER\",\"clicks\":\""+0+"\"},{\"id\":2,\"username\":\"Nazifa\",\"password\":\"Friend\",\"role\":\"FREE_USER\",\"clicks\":\""+0+"\"}]"));
    }

    @Test
    @Order(3)
    void getUserByUsernameExists() throws Exception{
        this.mockMvc.perform(get("/rest/api/user/get/Nick")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    void getUserByUsernameNotExists() throws Exception{
        this.mockMvc.perform(get("/rest/api/user/get/test")).andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void getUserByUsernameandPassword_WrongPass() throws  Exception{
        this.mockMvc.perform(get("/rest/api/user/get/Nick/Wrong")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void getUserByUsernameandPassword_NotExist() throws  Exception{
        this.mockMvc.perform(get("/rest/api/user/get/test/Wrong")).andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void getUserByUsernameandPassword_GoodCred() throws  Exception{
        JSONObject obj = new JSONObject();
        obj.put("username", "Test");
        obj.put("password","Check");
        this.mockMvc.perform(post("/rest/api/user/add").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(obj)));
        this.mockMvc.perform(get("/rest/api/user/get/Test/Check")).andExpect(status().isOk()).andExpect(content().json("{\"id\":3,\"username\":\"Test\",\"password\":\"Check\",\"role\":\"FREE_USER\",\"clicks\":\"0\"}"));
    }


    @Test
    @Order(8)
    void createUser() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("username","Nick");
        obj.put("password","Help");
        this.mockMvc.perform(post("/rest/api/user/add").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(obj))).andExpect(status().isCreated()).andExpect(content().json("{\"id\":4,\"username\":\"Nick\",\"password\":\"Help\",\"role\":\"FREE_USER\",\"clicks\":\"0\"}"));
    }

    @Test
    @Order(9)
    void changeRole() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("username","Jim");
        obj.put("password","pass1!");
        this.mockMvc.perform(post("/rest/api/user/add").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(obj))).andExpect(content().json("{\"id\":5,\"username\":\"Jim\",\"password\":\"pass1!\",\"role\":\"FREE_USER\"}"));
        JSONObject objt = new JSONObject();
        objt.put("role","ADMIN");
        this.mockMvc.perform(post("/rest/api/user/changeRole/"+5).contentType(MediaType.APPLICATION_JSON).content(String.valueOf(objt))).andExpect(status().isCreated()).andExpect(content().json("{\"id\":5,\"username\":\"Jim\",\"password\":\"pass1!\",\"role\":\"ADMIN\"}"));
    }

    @Test
    @Order(10)
    void upgradeUserRole() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("username","Cole");
        obj.put("password","train1!");
        String jsonString = "{\"id\":6,\"username\":\"Cole\",\"password\":\"train1!\",\"role\":\"FREE_USER\"}";
        this.mockMvc.perform(post("/rest/api/user/add").contentType(MediaType.APPLICATION_JSON).content(String.valueOf(obj))).andExpect(content().json(jsonString));
        JSONObject objt = new JSONObject();
        String jsonStringTexpected = "{\"id\":6,\"username\":\"Cole\",\"password\":\"train1!\",\"role\":\"PAID_USER\"}";
        this.mockMvc.perform(put("/rest/api/user/upgrade/"+6)
                .contentType(MediaType.APPLICATION_JSON).content(String.valueOf(objt)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonStringTexpected));
    }

    /*
    Tests an endpoint in UserRestController <- Made for use with the dynamic table element in view_users.html
    Endpoint returns Page object with list of UserPOJOS (sans passwords) and a few other stats.
    just tests that the return is 200 and the json is ok {"data":[],"recordsFiltered":6,"recordsTotal":6,"draw":0}
    I verify this works by manual integration testing of the site.
     */
    @Test
    @Order(11)
    void list() throws Exception {
        JSONObject obj = new JSONObject();
        String jsonStringTexpected = "{\"data\":[],\"recordsFiltered\":6,\"recordsTotal\":6,\"draw\":0}";
        this.mockMvc.perform(post("/rest/api/usertable")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(obj)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(jsonStringTexpected));
    }
}