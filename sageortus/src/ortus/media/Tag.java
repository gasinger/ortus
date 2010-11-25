package ortus.media;

import sagex.api.MediaFileAPI;


public class Tag
{
	
	public static boolean HasMediaTag(Object MediaObject,String Tag)
	{
				if (MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags").contains(";"+ Tag + ";"))
				{return true;}
				else
				{return false;}
	}
	public static String  SetMediaTag(Object MediaObject,String Tag)
	{
		    String result = null;
			if (MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags").contains(";" + Tag + ";")){
				result = "0";
							}
				
			else{
				String Tags = MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags");
				MediaFileAPI.SetMediaFileMetadata(MediaObject, "MediaTags", Tags +";" + Tag + ";");
				result = "1";
			}
				return result;
			}
	public static String RemoveMediaTag(Object MediaObject,String Tag)
			{
			if (MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags").contains(";" + Tag + ";"))
			{}
						   
					String CurrTags = MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags");
					String TagRemoved = null;
					String result = null;
					
					if (CurrTags != null)
					{
					TagRemoved = CurrTags.replaceAll(";"+Tag+";","");
															
					MediaFileAPI.SetMediaFileMetadata(MediaObject, "MediaTags", TagRemoved);	
					result = "1";							
			   }
			else {
			       result = "0";
			}
				return result;
			
				}
	public static boolean HasMediaTags(Object MediaObject)
	{
		String Tags = MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags");
		if (Tags.length() != 0 && Tags != null)
			{return true;}
		else 
			{return false;}
	}
	public static String[] GetMediaTags(Object MediaObject)
	     {
	          String [] tagArray = null;
	          String mediaTags = MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags");
	          if (mediaTags != null)
	          {
	              tagArray = mediaTags.split(";*;");
	              		
	          }
	          else
	          {tagArray = null;}
	          
	          return tagArray;
	     }
	public static String ClearMediaTags(Object MediaObject)
	{
		String tags = MediaFileAPI.GetMediaFileMetadata(MediaObject, "MediaTags");
		if (tags != null)
		{
		   MediaFileAPI.SetMediaFileMetadata(MediaObject, "MediaTags", null);
		   return "1";
		}
		else
		{
			return "0";
		}
	}
} 

	
			
	
	

	
	

	
	


