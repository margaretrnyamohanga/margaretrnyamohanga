package genius.code.bamfamily.junitrestapiapplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
// This annotation makes sure that this class is test ready and that we are using mockito to run test for this class
public class BookControllerTest {
    //The point of writing code for this controller is to test the controller and not the repository
    // Mocks are dummy representation for the class of interface/repository
    //the mocks are used to most and test the class and interface without hitting the diatabase all the time and ensure that the information or code is working as intended.
    private MockMvc mockMvc;// Mockmvc

    //
    ObjectMapper objectMapper = new ObjectMapper();// object mapper is used to convert a json into a string and vice versa reason we need to use it.
    ObjectWriter objectWriter = objectMapper.writer();// object writer is read and we can use it in all classed. not recommended but it helps on focusing on the unit test and not on anything else.

    @Mock
    private BookRepository bookRepository; // Now we have a mock read for the controller test.

    @InjectMocks
    private BookController bookController;// We enject the bookrepository as inject mocks into the bookcontroller


    //prepare test data
    Book RECORD_1 = new Book(1L, "Delicious meals", "How to cook healthy Kenya foods", 5);
    Book RECORD_2 = new Book(2l, "My journey as  Widow", "Prayers help through difficult and tough times of life", 5);
    Book RECORD_3 = new Book(3L, "The secret of cooking Nice and Delicious Pilau", "How to cook pilau in a very simple and easy way", 4);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void getAllReocrds_success() throws Exception {
        //We need to have a list of books of test cases or data created up there for testing.
        //But first we have a creat a list of books i.e

        //creat a list of book
        List<Book> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));// now we have the records as the intended output or value os the records created.

        //Mock book repository using the record list
        Mockito.when(bookRepository.findAll()).thenReturn(records); // Note that here we are planning to mocking the @GetMApping method to return the books form the repository using the Book controller class

        // We need to perform emilation of get request and this is where we use mvc to help us mock that.
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book")//get the cook url endpoint
                        .contentType(MediaType.APPLICATION_JSON))//content to be json
                .andExpect(status().isOk())// and now we do and expect status to be ok which is a 200 status. This particular request will build a request of book
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))// The next thing to expect is mockMvcResult matcher and what it says is that check the entire json and check if it has that size 3 of records
                .andExpect(jsonPath("$[2].name", is("The secret of cooking Nice and Delicious Pilau")))// This means go get the 3rd record of the records and give or check the that record name
                .andExpect(jsonPath("$[1].name", is("My journey as  Widow"))); //checking the 2nd record of array where value is of that name in string


    }

    @Test
    public void getBookById_success() throws Exception {
        Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(Optional.ofNullable(RECORD_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Delicious meals")));
    }

    @Test
    public void createRecord_success() throws Exception {//Here we need to first create a book record and the second thing will be to mock save()
        // and then when we want to do a post we want to make sure that the content is of String and then we do a perform request

        //create a record to post
        Book record = Book.builder()
                .bookId(4L)
                .name("Introduction to records Management")
                .summary("Management of records is very essential")
                .rating(5)
                .build();

        //Mock the repository with the save
        Mockito.when(bookRepository.save(record)).thenReturn(record);

        //using object mapper to convert java object into a string..

        String content = objectWriter.writeValueAsString(record);// This converts a plain old or object java into a string then use content to pass into content for post request line 119, then we sent the mockPostRequest item to be sent

        //instead of writing a request builder scenario here lets do it seperately and then do a perform

        //build a mock http servelet request builder to accomplish the seperation and perform
        MockHttpServletRequestBuilder mockPostRequest = MockMvcRequestBuilders.post("/book")// Do a mockhttpserverequestbuilder and we are posting to slash book
                .contentType(MediaType.APPLICATION_JSON)//note that once we do a  post request, we are returning back json to the client as well
                .accept(MediaType.APPLICATION_JSON)// hence we are accepting the content type as json as well.
                .content(content);// we cannot directly pass a java plain object(pojo) i.e book record created on line 99 into ... it has to be converted into a json first, so we this is where we need to use the object mapper to handle this... see line 109


        //next thing is to do a mockmvc to do a post to perform the request

        mockMvc.perform(mockPostRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Introduction to records Management")));// and pass in the value of the name on the record created up there for test
    }


    @Test
    public void updateRecord_success() throws Exception {
        // We need to update a book record. A few things to do are first
        // we have to mock 2 things here. meaning we are calling the book repository twice which is findById() method where we are passing the id and the request body and then save() method where we are saving it in the updated method
        // We use te external service twice we have to emulate and mock te book repository twice to see if everyting works well

        // First we need to have the book record which is already updated.i.e see code right below
        Book updatedRecord = Book.builder()
                .bookId(1L)
                .name("updated Book Name")
                .summary("updated summary")
                .rating(1).build();

        //We need to mock findbyid method first

        Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(Optional.ofNullable(RECORD_1));
        // mock the next part on the same method for the save()
        Mockito.when(bookRepository.save(updatedRecord)).thenReturn(updatedRecord);

        //convert the content into String by objectwriter ready to pass to json

        String updatedContent = objectWriter.writeValueAsString(updatedRecord);

        //next is to create a mock

        MockHttpServletRequestBuilder mockUpdateRequest = MockMvcRequestBuilders
                .put("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(updatedContent);// updated the content that we just converted into json up there here.

        //the las thing to do here is to perform a mock mvc to do a put request
        mockMvc.perform(mockUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("updated Book Name")));
    }

    //TDD unit tests

    //Write the delete method using the TDD

    @Test
    public void deleteBookById_success() throws Exception{// first we are going to do is that we need to use the book repository
        // to call find by id, we need to get the book first and then we delete it

        Mockito.when(bookRepository.findById(RECORD_2.getBookId())).thenReturn(Optional.of(RECORD_2));// when we get the book back, all that the delete endpoint is going to do is delete it.
        // we are not going to return what we deleted but what we can do is that we will just send a 200 status that we have deleted the  book id successfully.
        // Once we delete the book from the database, we will get a simple status code that the status is ok

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/book/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
