package com.indiehood.app;

        import com.indiehood.app.ui.listings.ShowListing;

        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.mockito.runners.MockitoJUnitRunner;

        import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MoreShowListingTests {
    @Test
    public void defaultConstructor_works() {
            ShowListing testListing = new ShowListing();
            assertThat(testListing).isNotNull();
    }

    //Add a band named "testBand" and check that it was added correctly
    @Test
    public void addBandName_works() {
        ShowListing testListing = new ShowListing();
        String showBand = "testBand";
        testListing.setBandName("testBand");
        assertThat(testListing.getBandName()).isEqualTo(showBand);
    }


    //Show that the getFreePrice function does not return a double value when the price is 0.0.
    //This function should refer to the getStringifiedPrice and change the double value into "FREE"
    @Test
    public void getFreePrice(){
        ShowListing testListing = new ShowListing();
        String freePrice = "0.0";
        testListing.setPrice(0.0);
        assertThat(testListing.priceFormatted).isNotEqualTo(freePrice);
    }

    //Show that if a setUserInterested is false, then the getUserInterested should not return True
    @Test
    public void addInterested_isFalse(){
        //should be false
        ShowListing testListing = new ShowListing();
        testListing.setUserInterested(false);
        assertThat(testListing.getUserInterested()).isFalse();
    }

    @Test
    //show that getPrice should return a double and not an integer
    //Two cases -> one Equal to and one Not Equal to
    public void getPriceDouble_works(){
        ShowListing testListing = new ShowListing();
        testListing.setPrice(10.0);
        Double price = 10.0;
        //Integer wrong = 10 ;
        assertThat(testListing.getPrice()).isEqualTo(price);
       //assertThat(testListing.getPrice()).isNotEqualTo(wrong);
    }

    //Show that when a band name is "" then it is empty.
    @Test
    public void emptyBandName_works(){
        ShowListing testListing = new ShowListing();
        String emptyName = "";
        testListing.setBandName(emptyName);
        assertThat(testListing.getBandName()).isEmpty();
    }
}