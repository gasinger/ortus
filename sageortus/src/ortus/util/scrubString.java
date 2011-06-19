package ortus.util;

import ortus.vars;
import sagex.api.MediaFileAPI;

import sagex.api.ShowAPI;
import sagex.api.AlbumAPI;

public class scrubString extends vars
{
//	// Scrubs "A " , "The" and "An" from start of AiringTitle for better sorting/grouping.
//
//	public static String GetAiringTitle(Object MediaObject)
//	{
//		return StringTitle(ortus.api.GetShowTitle(MediaObject));
//	}
//
//	// Scrubs "A " , "The" and "An" from start of MediaTitle for better sorting/grouping.
//
//	public static String GetMediaTitle(Object MediaObject)
//	{
//		return StringTitle(MediaFileAPI.GetMediaTitle(MediaObject));
//	}
//
//	// Scrubs "A " , "The" and "An" from start of ShowTitle for better sorting/grouping.
//
//	public static String GetShowTitle(Object MediaObject)
//	{
//		return StringTitle(ShowAPI.GetShowTitle(MediaObject));
//	}
//
//	// Scrubs "A " , "The" and "An" from start of ShowEpisode for better sorting/grouping.
//
//	public static String GetShowEpisode(Object MediaObject)
//	{
//		return StringTitle(ShowAPI.GetShowEpisode(MediaObject));
//	}
//
//	// Scrubs "A " , "The" and "An" from start of AlbumArtist for better sorting/grouping.
//
//	public static String GetAlbumArtist(Object MediaObject)
//	{
//		return StringTitle(AlbumAPI.GetAlbumArtist(MediaObject));
//	}
//
//	// Scrubs "A " , "The" and "An" from start of AlbumName for better sorting/grouping.
//
//	public static String GetAlbumName(Object MediaObject)
//	{
//		return StringTitle(AlbumAPI.GetAlbumName(MediaObject));
//	}
	
	// Returns the category with trailing "Film" removed.
	
	public static String GetShowCategory(Object MediaObject)
	{
		return StringCategory(ShowAPI.GetShowCategory(MediaObject));
	}
		
	//Returns the sub category with trailing "Film" removed.	
	
	public static String GetShowSubCategory(Object MediaObject)
	{
		return StringCategory(ShowAPI.GetShowSubCategory(MediaObject));
			}
	
	// Below gets Shows main category and sub category if existent returns "" if no category found for main category.
	//Useful for GroupByArrayMethod as only requires one call to get all categories. It also scrubs trailing "film" from the category
	// name so "Action" is grouped with category "Action Film".
	
	public static String[] GetShowCategories(Object MediaObject)
	{
		String Category = StringCategory(ShowAPI.GetShowCategory(MediaObject));
		String SubCategory = StringCategory(ShowAPI.GetShowSubCategory(MediaObject));
		String [] Categories;
				if (SubCategory.length() != 0)
		{ 
			
			Categories = new String[2];
			Categories[0] = Category;
			Categories[1] = SubCategory;
			
		}
		else if(Category.length() != 0)
		{  			
			Categories = new String[1];
			Categories[0] = Category;
		}
		else
			{ Categories = null;}
	
		return Categories;
	}
	
	// Below is simply for display purposes displays the Main category and sub category with " | " separation.
	// Also removes trailing "Film" from category name.
	
	public static String GetShowCombCategories(Object MediaObject)
	{
		String category = StringCategory(ShowAPI.GetShowCategory(MediaObject));
		String subcategory = StringCategory(ShowAPI.GetShowSubCategory(MediaObject));
		String categories = "";
		if (category.length()!= 0 && subcategory.length() != 0)
		{
		 categories = category + " | " + subcategory;
		}
		else if (category.length() != 0)
		{
			categories = category;
		}
		
		return categories;
			}
	
	//Below simply removes trailing "user ratings" or "---" from descriptions it is useful for a cleaner description look and scraps
	// either "user rating" or "--" and everything after it as that is usually repeated description.
	
	public static String GetShowDescription(Object MediaObject)
		{
			String s1 = ShowAPI.GetShowDescription(MediaObject);
		
			if (s1.contains("---") )
				{s1 = s1.substring(0,s1.indexOf("---"));		}
						
			else if(s1.contains("User")) 
								
			{ s1 =s1.substring(0,s1.indexOf("User"));
			}
		
		
			return s1;
		}
	
	// Below is a custom tagline if for just returning the first full sentence in a description of a show sense there is no real tagline
	// in metadata properties.
	
	public static String GetShowTagLine(Object MediaObject)
	{
		String s1 = ShowAPI.GetShowDescription(MediaObject);
		if(s1.contains("."))
		{s1 = s1.substring(0,s1.indexOf("."));}
		
		return s1;
	}
	
	// Below groups actors in show in a string array by removing the role from the actor and only returning
	// the actor name to the array. Useful for grouping actors throughout a collection as default will return actor -- role
	// making it impossible to group by actor name. Great for use with GroupByArrayMethod.
	
	public static String[] GetPeopleInShowRoleActor(Object MediaObject)
	{
		String[] actors = ShowAPI.GetPeopleListInShowInRole(MediaObject,"Actor");
		int length = actors.length;
		String[] actorlist = new String[length];
		if (length != 0)
		{	
		for ( int i = 0; i < length;i++)
		{
			String element = actors[i].trim();
			if(element.contains("--"))
			{
			actorlist[i] = element.substring(0,element.indexOf("--"));
		}  
			else
			{actorlist[i] = element;}
		}
		}
		else
			{actorlist = null;}
		
		
		return actorlist;
		
	}

	// below is simply grouping directors in string array.
	
	public static String[] GetPeopleInShowRoleDirector(Object MediaObject)
	{
		String[] directors = ShowAPI.GetPeopleListInShowInRole(MediaObject,"Director");
		int length = directors.length;
		String [] directorList = new String[length];
		
		if (length != 0)
		{	
		for ( int i = 0; i < length;i++)
		{
			String element = directors[i].trim();
			
			directorList[i] = element;}
		}
		else{
			directorList = null;}
		
		return directorList;
		
	}
	
	//Below to get actor name and scrub the role when using default getpeopleinshowactor call
	
	public static String GetActorName(String Actor)
	{
		if(Actor.contains("--"))
		{ Actor = Actor.substring(0,Actor.indexOf("--"));}
		
		return Actor;
			 
	}
	
	//Below to get Actor role and scrub actorname when using default getpeopleinshowactor call
	// returns "" if no role found
	
	public static String GetActorRole(String Actor)
	{
		if (Actor.contains("--"))
		{ Actor = Actor.substring(Actor.indexOf("--")+2,Actor.length());}
		
		return Actor;
	}
	
	//below is simple grouping writers of show in an string array.
	
	public static String[] GetPeopleInShowRoleWriter(Object MediaObject)
	{
		String[] writers = ShowAPI.GetPeopleListInShowInRole(MediaObject,"Writer");
		int length = writers.length;	
		String [] WriterList = new String[length];
		
		if (length != 0)
		{	
		for ( int i = 0; i < length;i++)
		{
			String element = writers[i].trim();
			
			WriterList[i] = element;}
		}
		else{
			WriterList = null;}
		
		return WriterList;		
		
	}
	
	// below Scrubs user rating from metadata fields by removing trail . and number for better assignment 
	// picture variables a.k.a star ratings
	
	public static String GetUserRating(Object MediaObject)
	{
		String Rating = MediaFileAPI.GetMediaFileMetadata(MediaObject,"UserRating");
		String Ratingscrubbed = "";
		if (Rating.contains(".") && Rating.length() != 0)
		{
			Ratingscrubbed = Rating.substring(0,Rating.indexOf("."));
		}
		else if (Rating.length() != 0 && !Rating.contains("awaiting"))
		{ Ratingscrubbed = Rating;}
		
		else
		{Ratingscrubbed = "0";}
		
		return Ratingscrubbed;
	}
	
	// below converts long string of show duration to a integer and returns that integer in minutes times
	// instead of the default long of ms time.
	
	public static int GetShowDuration(Object MediaObject)
	{
		long duration = ShowAPI.GetShowDuration(MediaObject);
		int durationint = (int) duration;
		if(durationint > 0)
		{ durationint = durationint/60000;
		 	}
		return durationint;	
				
	}
			
		
//	public static String StringTitle(String s1)
//	{
//		s1 = s1.toLowerCase();
//
//		if (s1.startsWith("the ") || s1.startsWith("the.") || s1.startsWith("the_"))
//		{
//			s1 = s1.substring(4).trim();
//		}
//		if (s1.startsWith("a ") || s1.startsWith("a.") || s1.startsWith("a_"))
//		{
//			s1 = s1.substring(2).trim();
//		}
//		if (s1.startsWith("an ") || s1.startsWith("an.") || s1.startsWith("an_"))
//		{
//			s1 = s1.substring(3).trim();
//		}
//
//
//                s1 = s1.replaceAll("_", "" ).trim();
//
//		return s1;
//	}

//        public static String ExtremeStringTitle(String s1)
//	{
////                DebugLog(TRACE2, "ExtremeStringClean: starting with: <" + s1+">");
//                s1 = s1.replaceAll("_WS", "");
//                s1 = s1.replaceAll("_LB","");
//                s1 = s1.replaceAll("_43","");
//                s1 = s1.replaceAll("_16X9LB","");
//                s1 = s1.replaceAll("_169","");
//                s1 = s1.replaceAll("_4X3FF","");
//                s1 = s1.replaceAll("_4X3LB","");
//                s1 = s1.replaceAll("UNRATED","");
//                s1 = s1.replaceAll("RATED","");
//
//		s1 = s1.toLowerCase();
//
//		if (s1.startsWith("the ") || s1.startsWith("the.") || s1.startsWith("the_"))
//		{
//			s1 = s1.substring(4).trim();
//		}
//		if (s1.startsWith("a ") || s1.startsWith("a.") || s1.startsWith("a_"))
//		{
//			s1 = s1.substring(2).trim();
//		}
//		if (s1.startsWith("an ") || s1.startsWith("an.") || s1.startsWith("an_"))
//		{
//			s1 = s1.substring(3).trim();
//		}
//                s1 = s1.replaceAll("\\.","");
//                s1 = s1.replaceAll("/","");
//                s1 = s1.replaceAll("\\?","");
//                s1 = s1.replaceAll("_", "" );
//                s1 = s1.replaceAll("-", " " );
//                s1 = s1.replaceAll("'","");
//                s1 = s1.replaceAll("\"","");
//                s1 = s1.replaceAll(",","");
//                s1 = s1.replaceAll(":","");
//                s1 = s1.replaceAll("!","");
//                s1 = s1.replaceAll("&", "and");
//                s1 = s1.replaceAll("\\s+", "").trim();
//
////                DebugLog(TRACE2, "ExtremeStringClean: returning: <" + s1 + ">");
//		return s1;
//	}


        
	private static String StringCategory(String s2)
	{
	
	 if (s2.length() == 0)
	 { s2 = "";}
					
	else if (s2.contains(" Film")) 
		{int ipos = s2.indexOf(" Film");
		  s2 = s2.substring(0,ipos);
		}
		return s2;
	}

	
	
	
}
