package com.Project100Pi.clip;

import android.app.Activity;


import android.view.Gravity;


import com.Project100Pi.clip.targets.ViewTarget;


public class Tutorial {
	void fullTutorial(int id,Activity current){
	ViewTarget target = new ViewTarget(id,current);
	ShowcaseView sc = new ShowcaseView.Builder(current)
	.setTarget(target)
    .setContentTitle("\nClipboard Clips")
    .setContentText("\nYou can copy text from any application and it will get stored as a clip")
    .hideOnTouchOutside()
    .setStyle(R.style.MyShowcaseView)
    .build();
	sc.setGravity(Gravity.LEFT);
}
}