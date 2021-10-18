package com.indiehood.app;

import com.indiehood.app.ui.artist_view.Artist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ArtistTests {
    @Test
    public void defaultConstructor_works() {
        Artist testArtist = new Artist();
        assertThat(testArtist).isNotNull();
    }

    /*@Mock TODO implement writeArtist test--mocks needed

    @Test
    public void writeNewArtist_works() {

    }*/

    @Test
    public void favorited_isTrue() {
        Artist testArtist = new Artist();
        // true case
        testArtist.setFavorited(true);
        assertThat(testArtist.getFavorited()).isTrue();
    }
    @Test
    public void favorited_isFalse() {
        Artist testArtist = new Artist();
        // false case
        testArtist.setFavorited(false);
        assertThat(testArtist.getFavorited()).isFalse();
    }
    @Test
    public void setRating_works() {
        Artist testArtist = new Artist();
        int rating = 5;
        testArtist.setRating(5);
        assertThat(testArtist.getRating()).isEqualTo(rating);
    }
    @Test
    public void setArtistName_works() {
        Artist testArtist = new Artist();
        String name = "TestArtist";
        testArtist.setArtistName("TestArtist");
        assertThat(testArtist.getArtistName()).isEqualTo(name);
        // empty string case
        testArtist.setArtistName("");
        assertThat(testArtist.getArtistName()).isEmpty();
    }
}