package com.bskyb.internettv;


import org.junit.Assert;
import org.junit.Test;

import com.bskyb.internettv.thirdparty.TechnicalFailureException;
import com.bskyb.internettv.thirdparty.TitleNotFoundException;
import com.bskyb.internettv.thirdparty.MovieService;
import com.bskyb.internettv.parental_control_service.ParentalControlService;
import com.bskyb.internettv.parental_control_service.impl.ParentalControlServiceImpl;

import static org.easymock.EasyMock.*;


public class ParentalControlServiceImplTest {
    MovieService mockMovieService;
    ParentalControlService service;

    @Test
    public void testLowerLevel() throws Exception {
        service = getServiceWithMockValues("movieId1", "PG");
        boolean actual = service.canWatchMovie( "U", "movieId1");
        Assert.assertEquals(false, actual);
    }

    private ParentalControlService getServiceWithMockValues(String movieId, String movieControlLevel) throws Exception {
        mockMovieService = createNiceMock(MovieService.class);
        expect(mockMovieService.getParentalControlLevel(movieId)).andReturn(movieControlLevel);
        replay(mockMovieService);
        return new ParentalControlServiceImpl(mockMovieService);
    }

    @Test
    public void testHigherLevel() throws Exception {
        service = getServiceWithMockValues("movieId1", "PG");
        boolean actual = service.canWatchMovie("TWELVE", "movieId1");
        Assert.assertEquals(true, actual);
    }

    @Test
    public void testHigherLevelAnother() throws Exception {
        service = getServiceWithMockValues("movieId1", "FIFTEEN");
        boolean actual = service.canWatchMovie("EIGHTEEN", "movieId1");
        Assert.assertEquals(true, actual);
    }

    @Test(expected = TitleNotFoundException.class)
    public void testNonExistingTitle() throws Exception {
        service = getServiceWithMockException("movieId1", new TitleNotFoundException());

        service.canWatchMovie("TWELVE", "movieId1");
    }

    private ParentalControlService getServiceWithMockException(String movieId, Exception e) throws Exception {
        mockMovieService = createNiceMock(MovieService.class);
        expect(mockMovieService.getParentalControlLevel(movieId)).andThrow(e);
        replay(mockMovieService);
        return new ParentalControlServiceImpl(mockMovieService);
    }

    @Test
    public void testSystemError() throws Exception {
        service = getServiceWithMockException("movieId2", new TechnicalFailureException());
        boolean actual = service.canWatchMovie("TWELVE", "movieId2");
        Assert.assertEquals(false, actual);
    }

    @Test
    public void testUndefinedControlLevel() throws Exception {
        service = getServiceWithMockValues("movieId1", "SomeUndefinedValue");
        boolean actual = service.canWatchMovie("TWELVE","movieId1");
        Assert.assertEquals(false, actual);
    }

    @Test
    public void testNullControlLevel() throws Exception {
        service = getServiceWithMockValues("movieId1", null);
        boolean actual = service.canWatchMovie("TWELVE", "movieId1");
        Assert.assertEquals(false, actual);
    }

    @Test
    public void testNullMovieId() throws Exception {
        service = getServiceWithMockValues(null, "PG");
        boolean actual = service.canWatchMovie("TWELVE", null);
        Assert.assertEquals(false, actual);
    }

    @Test
    public void testNullPreferredParentalControl() throws Exception {
        service = getServiceWithMockValues("movieId1", "PG");
        boolean actual = service.canWatchMovie("movieId1", null);
        Assert.assertEquals(false, actual);
    }

    @Test
    public void testUndefinedPreferredParentalControl() throws Exception {
        service = getServiceWithMockValues("movieId1", "PG");
        boolean actual = service.canWatchMovie("UNDEFINED", "movieId1");
        Assert.assertEquals(false, actual);
    }

    @Test
    public void testNullMovieService() throws Exception {
        service = new ParentalControlServiceImpl(null);
        boolean actual = service.canWatchMovie("UNDEFINED", "movieId1");
        Assert.assertEquals(false, actual);
    }

}
