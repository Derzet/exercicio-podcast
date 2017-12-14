package br.ufpe.cin.if710.podcast;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.ui.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.StringStartsWith.startsWith;
/**
 * Created by gabri on 12/12/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ListPodcastTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
    @Test
    public void testaSeExisteListViewInicial (){
        onView(withId(R.id.items)).check(matches(isDisplayed()));
    }

    @Test
    public void testarSeListViewClicavel(){
        onView(withId(R.id.items)).perform(click());
    }

    @Test
    public void testarPrimeiroElementoList() {
        onData(hasToString(startsWith("Ciência e Pseudociência")))
                .inAdapterView(withId(R.id.items)).atPosition(0)
                .perform(click());
    }
 /*   @Test
    public void testarDetailsElementoList(){
        onData(hasToString(startsWith("Ciência e Pseudociência")))
                .inAdapterView(withId(R.id.items)).atPosition(0)
                .perform(click(longClick()));
        onView(withId(R.id.link)).check(matches(withText("http://frontdaciencia.ufrgs.br/#1")));
    }*/
   /* @Test
    public void clickEmBaixar(){
        onData(hasToString(startsWith("Ciência e Pseudociência"))).onChildView(withId(R.id.item_action)).perform(click());
    }*/
}
