/**
 * SeeScore For Android Sample App
 * Dolphin Computing http://www.dolphin-com.co.uk
 */
package co.wlue.pageturner.utils;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import uk.co.dolphin_com.seescoreandroid.CursorView;
import uk.co.dolphin_com.seescoreandroid.SeeScoreView;
import uk.co.dolphin_com.seescoreandroid.SystemView;
import uk.co.dolphin_com.sscore.SScore;
import uk.co.dolphin_com.sscore.SSystem;

/**
 * SeeScoreView manages layout of a {@link SScore} and placement of {@link SystemView}s into a scrolling View.
 */
public class CustomSeeScoreView extends SeeScoreView {

	public CustomSeeScoreView(Activity context, CursorView cursorView, AssetManager am, ZoomNotification zn, TapNotification tn) {
		super(context, cursorView, am, zn, tn);
		this.currentLastSystem = 0;
		this.currentFirstSystem = 0;
		spaceHeight = 0;
		viewIsFull = false;
	}

	@Override
	protected void addSystem(final SSystem sys) {
		//Each system is 130 height (around) at scale 1.0F
		systems.addSystem(sys);
		showSystem(sys);
	}

	private void showSystem(final SSystem sys) {
		if(spaceLeftForSystems>0&&!viewIsFull) {
			new Handler(Looper.getMainLooper()).post(new Runnable(){

				public void run() {
					final SystemView sv = new SystemView(getContext(), cursorView, score, sys, CustomSeeScoreView.this.assetManager, tapNotify);
					addView(sv);
					views.add(sv);
					sv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							sv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
							if(sv.getHeight()>spaceLeftForSystems) {
								removeView(sv);
								views.remove(sv);
								currentLastSystem--;
								viewIsFull = true;
								Log.d("CurrentSystem2", currentLastSystem +"");
							} else {
								spaceLeftForSystems = spaceLeftForSystems - sv.getHeight();
							}
						}
					});
					currentLastSystem++;
					Log.d("CurrentSystem", currentLastSystem +"");
				}
			});
		}

	}

	private void showSystems() {
		currentFirstSystem = currentLastSystem;
		for(int i = currentLastSystem; i<systems.getSize() && !viewIsFull; i++) {
			final int index = i;
			new Handler(Looper.getMainLooper()).post(new Runnable(){

				public void run() {
					final SystemView sv = new SystemView(getContext(), cursorView, score, systems.getSystemAt(index), CustomSeeScoreView.this.assetManager, tapNotify);
					addView(sv);
					views.add(sv);
					sv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							sv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
							if(sv.getHeight()>spaceLeftForSystems) {
								removeView(sv);
								views.remove(sv);
								currentLastSystem--;
								viewIsFull = true;
								Log.d("CurrentSystem2", currentLastSystem +"");
							} else {
								spaceLeftForSystems = spaceLeftForSystems - sv.getHeight();
							}
						}
					});
					currentLastSystem++;
					Log.d("CurrentSystem", currentLastSystem +"");

				}
			});
		}

	}

	private void showPreviousSystems() {
		currentLastSystem = currentFirstSystem;

		for(int i = currentLastSystem-1; i>=0 && !viewIsFull; i--) {
			final int index = i;
			new Handler(Looper.getMainLooper()).post(new Runnable(){

				public void run() {
					final SystemView sv = new SystemView(getContext(), cursorView, score, systems.getSystemAt(index), CustomSeeScoreView.this.assetManager, tapNotify);
					addView(sv,0);
					views.add(0,sv);
					sv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							sv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
							if(sv.getHeight()>spaceLeftForSystems) {
								removeView(sv);
								views.remove(sv);
								currentFirstSystem++;
								viewIsFull = true;
								Log.d("CurrentSystem2", currentLastSystem +"");
							} else {
								spaceLeftForSystems = spaceLeftForSystems - sv.getHeight();
							}
						}
					});
					currentFirstSystem--;
					Log.d("CurrentSystem", currentLastSystem +"");

				}
			});
		}

	}

	public SystemView getSystemViewForBar(int barIndex) {
		for(int i = 0; i< views.size(); i++) {
			if(views.get(i).containsBar(barIndex)) {
				return views.get(i);
			}
		}
		return null;
	}

	public void nextPage() {
		Log.d("Systems",currentFirstSystem+" - " + currentLastSystem);
		if (!isAbortingLayout)
		{
			if(currentLastSystem <systems.getSize())
			{
//				layoutThread.abort();
//				systems = new SSystemList();
				removeAllViews();
				spaceLeftForSystems = spaceHeight;
				viewIsFull = false;
				showSystems();
//				layoutThread = new LayoutThread(screenHeight);
//				layoutThread.start();

			}
			else {
				Toast.makeText(getContext(),"This is the last page of the score",Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void previousPage() {
		if (!isAbortingLayout)
		{
			if(currentFirstSystem > 0)
			{
//				layoutThread.abort();
//				systems = new SSystemList();
				removeAllViews();
				spaceLeftForSystems = spaceHeight;
				viewIsFull = false;
				showPreviousSystems();
//				layoutThread = new LayoutThread(screenHeight);
//				layoutThread.start();

			}
			else {
				Toast.makeText(getContext(),"This is the first page of the score",Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void setHeight(int height) {
		spaceHeight = height;
		Log.d("Height",height+"");
	}

	//TODO: Check if this variable still works or if there is a newer one
    private boolean isAbortingLayout = false;
	private int currentLastSystem;
	private int currentFirstSystem;
	private float spaceLeftForSystems;
	private float spaceHeight;
	private boolean viewIsFull;
}
