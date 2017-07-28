package com.mayulive.swiftkeyexi.xposed.keyboard;

/**
 * Created by Roughy on 2/15/2017.
 */

import android.view.inputmethod.InputConnection;

import com.mayulive.swiftkeyexi.xposed.Hooks;
import com.mayulive.swiftkeyexi.xposed.key.KeyProfiles;
import com.mayulive.xposed.classhunter.ClassHunter;
import com.mayulive.xposed.classhunter.profiles.ClassItem;
import com.mayulive.xposed.classhunter.profiles.MethodProfile;
import com.mayulive.xposed.classhunter.ProfileHelpers;
import com.mayulive.xposed.classhunter.packagetree.PackageTree;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.mayulive.xposed.classhunter.Modifiers.*;


public class KeyboardClassManager
{

	/////////////////
	//Known classes
	/////////////////

	public static Class layoutClass = null;
	public static Class keyboardServiceClass =  null;
	public static Class breadcrumbClass = null;

	/////////////////
	//unknown classes
	/////////////////

	public static Class keyboardLoaderClass = null;

	/////////////////////////
	//Methods
	/////////////////////////
	public static Method getCurrentInputConnectionMethod = null;
	public static Method keyboardLoader_onSharedPreferenceChangedMethod;
	public static Method keyboardLoader_loadMethod = null;
	public static Method keyboardLoader_clearCacheMethod = null;



	////////////////////
	//Fields
	////////////////////
	public static Field keyboardLoaderClass_layoutField = null;


	///////////////////
	//Objects and instances
	//////////////////
	public static Object keyboardServiceInstance = null;
	public static InputConnection currentInputConnection = null;


	public static void loadKnownClasses(PackageTree param)
	{
		keyboardServiceClass = ClassHunter.loadClass("com.touchtype.KeyboardService", param.getClassLoader());
		breadcrumbClass = ClassHunter.loadClass("com.touchtype.telemetry.Breadcrumb", param.getClassLoader());
		layoutClass = ClassHunter.loadClass("com.touchtype_fluency.service.languagepacks.layouts.LayoutData.Layout", param.getClassLoader());

	}

	public static void loadUnknownClasses(PackageTree param)
	{
		keyboardLoaderClass = ProfileHelpers.loadProfiledClass( KeyProfiles.get_KEYBOARD_LOADER_CLASS_PROFILE(), param );
	}


	public static void loadMethods() throws NoSuchMethodException
	{
		if (keyboardServiceClass != null)
		{
			getCurrentInputConnectionMethod = KeyboardClassManager.keyboardServiceClass.getMethod("getCurrentInputConnection", (Class[])null);
		}

		if (keyboardLoaderClass != null)
		{

			keyboardLoader_onSharedPreferenceChangedMethod = ProfileHelpers.findFirstMethodByName(keyboardLoaderClass.getDeclaredMethods(), "onSharedPreferenceChanged");

			keyboardLoader_loadMethod = ProfileHelpers.findMostSimilar(

					new MethodProfile
							(
									PRIVATE | EXACT ,
									new ClassItem("com.touchtype.keyboard" , PUBLIC | ABSTRACT | ARRAY | EXACT ),

									new ClassItem("com.touchtype.telemetry.Breadcrumb" , PUBLIC | EXACT ),
									new ClassItem("com.touchtype.keyboard" , PUBLIC | ARRAY | EXACT ),
									new ClassItem("com.touchtype.keyboard" , PUBLIC | STATIC | EXACT ),
									new ClassItem("com.touchtype_fluency.service.languagepacks.layouts.LayoutData.Layout" , PUBLIC | STATIC | FINAL | ENUM | EXACT ),
									new ClassItem(int[].class),
									new ClassItem("com.touchtype.keyboard" , PUBLIC | EXACT ),
									new ClassItem(boolean.class)
							),

					KeyboardClassManager.keyboardLoaderClass.getDeclaredMethods(), KeyboardClassManager.keyboardLoaderClass);

			keyboardLoader_clearCacheMethod = ProfileHelpers.findFirstProfileMatch(

					new MethodProfile
							(
									PRIVATE | EXACT ,
									new ClassItem(void.class ),
									new ClassItem[0]

							),

					KeyboardClassManager.keyboardLoaderClass.getDeclaredMethods(), KeyboardClassManager.keyboardLoaderClass);


		}

	}

	public static void loadFields()
	{
		keyboardLoaderClass_layoutField = ProfileHelpers.findFirstDeclaredFieldWithType( layoutClass, KeyboardClassManager.keyboardLoaderClass);
		keyboardLoaderClass_layoutField.setAccessible(true);
	}


	public static void doAllTheThings(PackageTree param) throws IOException, NoSuchFieldException, NoSuchMethodException
	{

		loadKnownClasses(param);
		loadUnknownClasses(param);
		loadMethods();
		loadFields();

		//Util.printFieldValues(KeyboardClassManager.class);

		updateDependencyState();
	}

	public static InputConnection getInputConnection()
	{
		try
		{
			return currentInputConnection = (InputConnection)getCurrentInputConnectionMethod.invoke(keyboardServiceInstance, (Object[])null);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();

			return null;
		}
	}

	protected static void updateDependencyState()
	{
		//Base
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_base,	 "KeyboardServiceClass", 	keyboardServiceClass );
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_base,	 "breadcrumbClass", 	breadcrumbClass );
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_base,	 "getCurrentInputConnectionMethod", 	getCurrentInputConnectionMethod );
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_base,	 "keyboardLoaderClass", 	keyboardLoaderClass );
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_base,	 "keyboardLoader_clearCacheMethod", 	keyboardLoader_clearCacheMethod );

		//View created (Overlay)
		Hooks.logSetRequirementFalseIfNull( Hooks.overlayHooks_base,	 "KeyboardServiceClass", 	keyboardServiceClass );

		//Popup
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_invalidateLayout,	 "keyboardLoader_onSharedPreferenceChangedMethod", 	keyboardLoader_onSharedPreferenceChangedMethod );

		//Hitbox
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_layoutChange,	 "layoutClass", 	layoutClass );
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_layoutChange,	 "keyboardLoader_loadMethod", 	keyboardLoader_loadMethod );
		Hooks.logSetRequirementFalseIfNull( Hooks.baseHooks_layoutChange,	 "keyboardLoaderClass_layoutField", 	keyboardLoaderClass_layoutField );
	}
}