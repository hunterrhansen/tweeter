import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOException;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.model.StatusDBData;
import edu.byu.cs.tweeter.server.service.StoryService;
import edu.byu.cs.tweeter.util.FakeData;

public class TestStoryService {
    private StoryService storyServiceSpy;
    private StatusDAO mockStatusDAO;
    private UserDAO mockUserDAO;
    private FakeData fakeData;
    private List<StatusDBData> fakeStatusData;
    private List<User> fakeUsers;

    @Before
    public void setup() throws DAOException {
        fakeData = new FakeData();
        fakeStatusData = new ArrayList<>();
        fakeUsers = new ArrayList<>();

        fakeStatusData.add(new StatusDBData(fakeData.getFakeStatuses().get(0), fakeData.getFakeUsers().get(0).getAlias()));
        fakeStatusData.add(new StatusDBData(fakeData.getFakeStatuses().get(1), fakeData.getFakeUsers().get(1).getAlias()));
        fakeStatusData.add(new StatusDBData(fakeData.getFakeStatuses().get(2), fakeData.getFakeUsers().get(2).getAlias()));

        fakeUsers.add(fakeData.getFakeUsers().get(0));
        fakeUsers.add(fakeData.getFakeUsers().get(1));
        fakeUsers.add(fakeData.getFakeUsers().get(2));

        storyServiceSpy = Mockito.spy(StoryService.class);
        mockStatusDAO = Mockito.mock(StatusDAO.class);
        mockUserDAO = Mockito.mock(UserDAO.class);

        Mockito.when(mockUserDAO.authenticate(Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(mockStatusDAO.getStory(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(this.fakeStatusData);
        Mockito.when(mockUserDAO.batchGetUsers(Mockito.anyList())).thenReturn(this.fakeUsers);


        Mockito.when(storyServiceSpy.getStatusDAO()).thenReturn(this.mockStatusDAO);
        Mockito.when(storyServiceSpy.getUserDAO()).thenReturn(this.mockUserDAO);
    }

    @Test
    public void testStoryService() {
        GetStoryRequest getStoryRequest = new GetStoryRequest(fakeData.getFirstUser(), new AuthToken(), 5, null);
        GetStoryResponse getStoryResponse = storyServiceSpy.getStory(getStoryRequest);

        Assert.assertTrue(getStoryResponse.isSuccess());
        Assert.assertNotNull(getStoryResponse.getItems());
        Assert.assertEquals(3, getStoryResponse.getItems().size());
    }

    @Test
    public void testInvalidRequest() {
        GetStoryRequest getStoryRequest = new GetStoryRequest(fakeData.getFirstUser(), null, -1, null);
        Assert.assertThrows(
                RuntimeException.class,
                () -> this.storyServiceSpy.getStory(getStoryRequest)
        );
    }
}
