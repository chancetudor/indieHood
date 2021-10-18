package com.indiehood.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UserTests {
    private User user = new User();
    private List<String> bands = new ArrayList<>();

    @Test
    public void setFavoritedBands_works() {
        bands.add("test 1");
        bands.add("test 2");
        bands.add("test 3");
        user.setFavoritedBands(bands);
        assertThat(user.getFavoritedBands()).containsExactlyElementsIn(bands);
    }

    @Test
    public void getUID_works() {
        user.setUID("testUID");
        assertThat(user.getUID()).isEqualTo("testUID");
    }

    @Test
    public void getArtist_works() {
        user.setArtist("testArtist");
        assertThat(user.getArtist()).isEqualTo("testArtist");
    }

    @Test
    public void addFavoritedBand_works() {
        user.addFavoritedBand("favBand");
        assertThat(user.getFavoritedBands()).contains("favBand");
    }

    @Test
    public void removeFavoritedBand_works() {
        user.removeFavoritedBand("favBand");
        assertThat(user.getFavoritedBands()).doesNotContain("favBand");
    }
}