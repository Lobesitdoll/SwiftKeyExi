package com.mayulive.swiftkeyexi.xposed;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mayulive.swiftkeyexi.ExiModule;
import com.mayulive.swiftkeyexi.main.commons.data.KeyType;
import com.mayulive.swiftkeyexi.xposed.emoji.EmojiCommons;
import com.mayulive.swiftkeyexi.xposed.hardwarekeys.HardwareKeyHooks;
import com.mayulive.swiftkeyexi.xposed.predictions.PredictionHooks;
import com.mayulive.swiftkeyexi.main.commons.data.KeyDefinition;
import com.mayulive.swiftkeyexi.settings.Settings;
import com.mayulive.swiftkeyexi.xposed.emoji.EmojiHooks;
import com.mayulive.swiftkeyexi.xposed.key.KeyCommons;
import com.mayulive.swiftkeyexi.xposed.key.KeyHooks;
import com.mayulive.swiftkeyexi.xposed.keyboard.KeyboardHooks;
import com.mayulive.swiftkeyexi.xposed.keyboard.KeyboardMethods;
import com.mayulive.swiftkeyexi.xposed.popupkeys.PopupkeysHooks;
import com.mayulive.swiftkeyexi.xposed.predictions.PredictionCommons;
import com.mayulive.swiftkeyexi.xposed.selection.SelectionHooks;
import com.mayulive.swiftkeyexi.xposed.sound.SoundHooks;
import com.mayulive.xposed.classhunter.packagetree.PackageTree;

/**
 * Created by Roughy on 6/22/2017.
 */

public class Hooks
{
	private static String LOGTAG = ExiModule.getLogTag(Hooks.class);

	//Predictions
	public static HookCategory predictionHooks_more = new HookCategory("PredictionHooks More");
	public static HookCategory predictionHooks_priority = new HookCategory("PredictionHooks Priority");
	public static HookCategory predictionHooks_base = new HookCategory("PredictionHooks", predictionHooks_more, predictionHooks_priority);
	public static HookCategory predictionHooks_candidate = new HookCategory("PredictionHooks Candidate", predictionHooks_base);

	//Selection
	public static HookCategory selectionHooks_base = new HookCategory("SelectionHooks");

	//Popups
	public static HookCategory popupHooks_delay = new HookCategory("PopupHooks Delay");
	public static HookCategory popupHooks_cancel = new HookCategory("PopupHooks Cancel", selectionHooks_base);
	public static HookCategory popupHooks_modify = new HookCategory("PopupHooks Modify");
	public static HookCategory popupHooks_read = new HookCategory("PopupHooks Read", popupHooks_modify);

	//Emoji
	public static HookCategory gifHooksNSFW = new HookCategory("GifHooks NSFW");
	public static HookCategory emojiHooks_base = new HookCategory("EmojiHooks Base");

	//Key
	public static HookCategory keyHooks_keyCancel = new HookCategory("KeyHooks Cancel");
	public static HookCategory keyHooks_keyDefinition = new HookCategory("KeyHooks Definition", keyHooks_keyCancel, selectionHooks_base);

	//Overlay (Doesn't contain any hooks, just dependencies)
	public static HookCategory overlayHooks_base = new HookCategory("overlayHooks base");

	//Keyboard
	public static HookCategory baseHooks_fullscreenMode = new HookCategory("KeyboardHooks fullscreenMode");
	public static HookCategory baseHooks_theme= new HookCategory("KeyboardHooks Theme");
	public static HookCategory baseHooks_viewCreated= new HookCategory("KeyboardHooks ViewCreated", overlayHooks_base);
	public static HookCategory baseHooks_invalidateLayout = new HookCategory("KeyboardHooks InvalidateLayout", popupHooks_modify);
	public static HookCategory baseHooks_layoutChange = new HookCategory("KeyboardHooks LayoutChange", overlayHooks_base);
	public static HookCategory baseHooks_punctuationSpace = new HookCategory("KeyboardHooks PunctuationSpace");
	public static HookCategory baseHooks_keyHeight = new HookCategory("KeyboardHooks keyHeight");

	//Sound
	public static HookCategory soundHooks_base = new HookCategory("SoundHooks base");

	//Hardware keyboard shortcuts and remapping
	public static HookCategory hardwareKeys_base = new HookCategory("Hardkey SHortcuts base");

	//Base, unhook everything.
	public static HookCategory baseHooks_base = new HookCategory("KeyboardHooks base", 	baseHooks_layoutChange,
																						baseHooks_invalidateLayout,
																						keyHooks_keyDefinition,
																						emojiHooks_base,
																						predictionHooks_base,
																						baseHooks_punctuationSpace,
																						baseHooks_theme,
																						soundHooks_base,
																						baseHooks_fullscreenMode,
																						baseHooks_keyHeight,
			hardwareKeys_base
	);

	//Convenience method for checking requirement and logging on failure
	public static void logSetRequirement( HookCategory category, String name, boolean newValue)
	{
		if ( !category.setRequirementsMet( newValue ))
		{
			Log.e(LOGTAG, "Pre-Hook failure: "+name+": Unexpected Value");
		}
	}

	//Convenience method for checking requirement and logging on failure
	//Sets to false if input value is null
	public static void logSetRequirementFalseIfNull(HookCategory category, String name, @Nullable Object value)
	{
		if ( !category.setRequirementsMet( value != null ))
		{
			Log.e(LOGTAG, "Pre-Hook failure: "+name+": Unexpected Value");
		}
	}


	public static void hookAll(PackageTree classTree)
	{

		//If this fails we are screwed
		KeyboardHooks.HookAll(classTree);

		if (Hooks.baseHooks_base.isRequirementsMet())
		{

			HardwareKeyHooks.HookAll(classTree);

			//No a hook, just sets a listener
			preventPeriodHook();

			SoundHooks.hookAll(classTree);

			//Emojis do not depend on anything else
			EmojiHooks.HookAll(classTree);

			KeyHooks.HookAll(classTree);

			//Predictions also fairly independent
			PredictionHooks.HookAll(classTree);


			//Selection requires popups to keep track of
			//keys with multiple popups.
			PopupkeysHooks.hookAll(classTree);


			SelectionHooks.hookAll(classTree);


			//While loading needs to be done /immediatel/ and is thus handled on the main
			//thread by each individual hook, we don't want to be too wasteful about saving
			//things that need to saved, so that is handled with a single thread call here.

			//The other ones could of course be handled with futures and all that, but they are
			//only updated when the user changes something, so it's hardly worth the effort.
			KeyboardMethods.addKeyboardEventListener(new KeyboardMethods.KeyboardEventListener()
			{
				@Override
				public void beforeKeyboardOpened() {}

				@Override
				public void beforeKeyboardClosed()
				{
					AsyncTask.execute(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{

								//These will run every time the keyboard closest, so very often.
								//They shouldn't fail, but if they do it won't be a disaster.
								//What would be a disaster is them crashing swiftkey in the process.
								PredictionCommons.savePriority();
								EmojiCommons.saveRecents();
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
								Log.e(LOGTAG, "Something went wrong performing keyboardc-close duties. Not fatal, but please report.");
							}


						}
					});
				}

				@Override
				public void keyboardInvalidated()
				{

				}

				@Override
				public void afterKeyboardConfigurationChanged() {}
			});
		}
	}



	private static void preventPeriodHook()
	{
		KeyCommons.addOnKeyDownListener(new KeyCommons.OnKeyDownListener()
		{
			@Override
			public void onKeyDown(KeyDefinition key)
			{
				if (key.is(KeyType.PERIOD) && Settings.DISABLE_PERIOD_CLICK)
				{

					KeyCommons.requestCancelNextKey();

					//Unfortunately, period key does not abide by the popup to trigger
					//PopupkeysCommons.requestPopupDelay();
				}
			}
		});
	}
}
