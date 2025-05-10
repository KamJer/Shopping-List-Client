package pl.kamjer.shoppinglist.util;

import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.kamjer.shoppinglist.viewmodel.ShoppingListViewModel;

public class TutorialManager {

    private final FrameLayout[] frameLayoutsOverlays;
    private final FloatingActionButton[] nextButtons;
    private final ShoppingListViewModel shoppingListViewModel;

    public TutorialManager(ShoppingListViewModel shoppingListViewModel, FrameLayout[] frameLayoutsOverlays, FloatingActionButton[] nextButtons) {
        this.shoppingListViewModel = shoppingListViewModel;
        this.frameLayoutsOverlays = frameLayoutsOverlays;
        this.nextButtons = nextButtons;
    }

    public void runOverlayTutorial() {
//        make all overlays invisible but the first one
        for (int i = 1; i < frameLayoutsOverlays.length; i++) {
            frameLayoutsOverlays[i].setVisibility(View.GONE);
        }

//        setting action for each next button and final one
        for (int i = 0; i < nextButtons.length; i++) {
            int finalI = i;
            nextButtons[i].setOnClickListener(v -> {
                frameLayoutsOverlays[finalI].setVisibility(View.GONE);
                if (finalI + 1 < frameLayoutsOverlays.length) {
                    frameLayoutsOverlays[finalI + 1].setVisibility(View.VISIBLE);
                }
            });
        }

//        if tutorial was seen, hide the first overlay
        if (shoppingListViewModel.isTutorialSeen()) {
            frameLayoutsOverlays[0].setVisibility(View.GONE);
        } else {
            shoppingListViewModel.tutorialSeen(true);
        }
    }
}
