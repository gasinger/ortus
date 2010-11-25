package ortus.media;

import sagex.api.*;
import java.lang.Character.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import ortus.api;
//import java.lang.Math;



public class Search
{
    //@SuppressWarnings("null")
    static C_SageFilters sf = new C_SageFilters();

    /*
     *Class that defines all the arguments for Sage's built in filtering functions
     * Not all
     *
    */
    static class C_SearchArg
    {
        //General - Defines filter type
        String Type;

        //All Filter Types (ByBoolMethod is only these)
        String Method;
        boolean MatchedPasses;

        //ByMethod
        Object MatchValue;

        //ByRegexMethod
        Pattern RegexPattern;
        boolean CompleteMatch;

        //ByRange
        Comparable LowerBoundInclusive;
        Comparable UpperBoundExclusive;
    }

	static class C_ReturnOpts
	{
		String SearchString;
		String OptsString;
		boolean OptsExist;
		String[][] OptsArray;

/*
		String Sort;
		String SortLexical;
		String GroupByMethod;
		String GroupByArrayMethod;
		String MaxNumber;
*/
 }

    static class C_SplitString
    {
        String Prefix;
        String Suffix;
        char Separator;
    }

    static class C_SageFilters
    {
        static HashMap<String,String> SageFilters = new HashMap<String,String>();
        
        public C_SageFilters() {
            LoadFilters("sagex.api.MediaFileAPI");
            LoadFilters("sagex.api.ShowAPI");
            LoadFilters("sagex.api.AiringAPI");
            LoadFilters("sagex.api.AlbumAPI");
        }

        private void LoadFilters(String classname) {
            try {
               Object[] x = Class.forName(classname).getMethods();
                for ( Object y : x ) {
                    Method z = (Method) y;
                    String mn = z.getName();
                    if ( mn.startsWith("Is"))
                         SageFilters.put(mn, "BoolMethod");
                    if ( mn.startsWith("Get"))
                         SageFilters.put(mn, "MethodRegex");
                }

            } catch (Exception e) {
               api.DebugLog("ERROR", "LoadFilters - Sage Filter: " + e );
            }
        }

        public String GetFilter(String filter) {
            if ( SageFilters.get(filter) == null )
               return "";
            else
                return SageFilters.get(filter);
        }

    }

    public static Object search(Object[] SourceList, String SearchString, String SortMethod)
    {
        api.DebugLog("INFO", "ortus.api.search - BEGIN");
        api.DebugLog("INFO", "SearchString: \"" + SearchString + "\"");
		api.DebugLog("INFO","Source Size = " + SourceList.length );

		Object Output = null;

		C_ReturnOpts RetOpts = ParseReturnOpts(SearchString);

		if (RetOpts.OptsExist){
			SearchString = RetOpts.SearchString;
			api.DebugLog("INFO", "Return Options Exist");
			api.DebugLog("INFO", "SearchString: \"" + SearchString + "\"");
			api.DebugLog("INFO", "Options: \"" + RetOpts.OptsString + "\"");
		}

		long t0 = System.currentTimeMillis();

		if (SearchString.isEmpty()){
			api.DebugLog("WARNING", "SearchString is empty");
			Output = SourceList;
		} else {
			Output = ParseSearchString(SourceList, SearchString.trim());
		}

        long t1 = System.currentTimeMillis() - t0;
		api.DebugLog("INFO","Output Size = " + ((Object[])Output).length );
        api.DebugLog("INFO","Search Execution Time = " + t1 + " ms" );


		if (RetOpts.OptsExist){
			Output = EvalReturnOpts(Output, RetOpts);
			long t2 = System.currentTimeMillis() - t0 - t1;
			api.DebugLog("INFO","Process Return Options Execution Time = " + t2 + " ms" );
		}
 /*
		if(!SortMethod.isEmpty()){
			SortMethod = SortMethod.trim();
			api.DebugLog("INFO", "SortMethod: \"" + SortMethod + "\"");
			boolean ReverseSort;
			if (SortMethod.startsWith("!")){
				ReverseSort = true;
				SortMethod = SortMethod.substring(1).trim();
			} else{
				ReverseSort = false;
			}
			Output = (Object[])Database.Sort((Object)Output, ReverseSort, SortMethod);

			long t2 = System.currentTimeMillis() - t1;
			api.DebugLog("INFO","Sort Execution Time = " + t2 + " ms" );
		}
*/
		long ttotal = System.currentTimeMillis() - t0;
		api.DebugLog("INFO","ortus.api.search Total Execution Time = " + ttotal + " ms" );
        api.DebugLog("INFO", "ortus.api.search - END");
        return Output;
    }

	public static Map search(Object[] Source, String SearchString, String SortMethod, String GroupByMethod)
	{
		Object Output = search(Source, SearchString, SortMethod);

		api.DebugLog("INFO", "ortus.api.search Grouping - BEGIN");
		api.DebugLog("INFO", "GroupByMethod: \"" + GroupByMethod + "\"");
		long t0 = System.currentTimeMillis();

		Map OutputMap = Database.GroupByMethod(Output, GroupByMethod);
		long t1 = System.currentTimeMillis() - t0;

		api.DebugLog("INFO","Group Execution Time = " + t1 + " ms" );

		api.DebugLog("INFO", "ortus.api.search Grouping - END");
		return OutputMap;

	}

	private static C_ReturnOpts ParseReturnOpts(String SearchString)
	{
		api.DebugLog("TRACE", "ParseReturnOpts - BEGIN");
		C_ReturnOpts RetOpts = new C_ReturnOpts();

		Pattern RegexPattern = Pattern.compile("(<)(\\s*)((Sort)|(SortLexical)|(GroupByMethod)|(GroupByArrayMethod)|(MaxNumber))(\\s*)(=)(.*)(>)");
		Matcher RegexMatcher = RegexPattern.matcher(SearchString);

		if (RegexMatcher.find()) {
			api.DebugLog("TRACE", "ParseReturnOpts - Optional return string found: \"" + RegexMatcher.group() + "\" Start index = " +
					RegexMatcher.start() + ", End index = " + RegexMatcher.end());

			RetOpts.OptsExist = true;
			String OptsString = RegexMatcher.group();
			RetOpts.SearchString = SearchString.replace(OptsString, "").trim();
			OptsString = OptsString.substring(1, OptsString.length()-1).trim();
			RetOpts.OptsString = OptsString;

			String[] OptSplit = OptsString.split(",");
			RetOpts.OptsArray = new String[OptSplit.length][2];
			String[] temp;

			for (int i=0; i<OptSplit.length; i++){
				api.DebugLog("TRACE2", "ParseReturnOpts - Optional return argument: \"" + OptSplit[i].trim() + "\"");
				temp = OptSplit[i].split("=");

				RetOpts.OptsArray[i][0] = temp[0].trim();
				RetOpts.OptsArray[i][1] = temp[1].trim();
				api.DebugLog("TRACE2", "ParseReturnOpts - Optional return argument key: \"" + RetOpts.OptsArray[i][0] + "\"");
				api.DebugLog("TRACE2", "ParseReturnOpts - Optional return argument value: \"" + RetOpts.OptsArray[i][1] + "\"");
/*
				if (Opt[0].equals("Sort")){
					RetOpts.Sort = Opt[1];
				}else if (Opt[0].equals("SortLexical")){
					RetOpts.SortLexical = Opt[1];
				}else if (Opt[0].equals("GroupByMethod")){
					RetOpts.GroupByMethod = Opt[1];
				}else if (Opt[0].equals("GroupByArrayMethod")){
					RetOpts.GroupByArrayMethod = Opt[1];
				}else if (Opt[0].equals("MaxNumber")){
					RetOpts.MaxNumber = Opt[1];
				}else{
					api.DebugLog("WARNING", "ParseReturnOpts - Unknown Optional return argument: \"" + Opt[0] + "\"");
				}
*/
			}

		} else{
			api.DebugLog("TRACE", "ParseReturnOpts - No optional return arguments found");
			RetOpts.SearchString = SearchString;
			RetOpts.OptsExist = false;
		}
		api.DebugLog("TRACE", "ParseReturnOpts - END");
		return RetOpts;
	}


	private static Object[] ParseSearchString(Object[] SourceList, String SearchString)
	{
		C_SplitString SearchStruct = ParseSubString(SearchString);

		if (!SearchStruct.Suffix.isEmpty()) {
			switch (SearchStruct.Separator) {
				case '&':
					//System.out.println("Separator: &");
					return ParseSearchString(
							ParseSearchString(SourceList, SearchStruct.Prefix),
							SearchStruct.Suffix);
				case '|':
					//System.out.println("Separator: |");
					return Database.DataUnion(
							ParseSearchString(SourceList, SearchStruct.Prefix),
							ParseSearchString(SourceList, SearchStruct.Suffix)).toArray();
				default:
					return null;
			}
		} else {
			return EvalArg(SourceList, ParseArg(SearchStruct.Prefix));
		}
	}

	private static C_SplitString ParseSubString(String SearchString)
	{
		api.DebugLog("TRACE", "ParseSubString - BEGIN");
		api.DebugLog("TRACE", "ParseSubString - SearchString: \"" + SearchString + "\"");
		api.DebugLog("TRACE2", "ParseSubString - SearchString Length: " + SearchString.length());

		//C_SplitString SearchStruct = new C_SplitString();

		int bal = 0;
		int i = -1;
		String c = null;

		if (SearchString.startsWith("<")) {
			api.DebugLog("TRACE2", "ParseNestedSubString - BEGIN");
			do {
				i++;
				c = Character.toString(SearchString.charAt(i));
				if (c.equals("<")) {
					bal++;
				} else if (c.equals(">")) {
					bal--;
				}
				api.DebugLog("TRACE2", "ParseNestedSubString - Char: \"" + c + "\" at i = " + i + ", Bal = " + bal);
			}while ( !((c.equals("|") || c.equals("&") ) && (bal == 0)) && ( i < SearchString.length() - 1 ) );

			if (bal == 0) {
				api.DebugLog("TRACE2", "ParseNestedSubString - END");
			} else {
				api.DebugLog("ERROR", "ParseNestedSubString - Unbalanced Argument Nesting");
			}

			if (i == SearchString.length() - 1 ){
				return ParseSubString(SearchString.substring(1, i).trim());
			}

		} else {
			api.DebugLog("TRACE2", "ParseNormalSubString - BEGIN");

			do {
				i++;
				c = Character.toString(SearchString.charAt(i));
				if (c.equals("'") && bal==0) {
					bal = 1;
				} else if (c.equals("'")) {
					bal = 0;
				}
				api.DebugLog("TRACE2", "ParseNormalSubString - Char: \"" + c + "\" at i = " + i + ", Bal = " + bal);
			}while ( !((c.equals("|") || c.equals("&") ) && (bal == 0)) && ( i < SearchString.length() - 1 ) );

			if (bal == 0) {
				api.DebugLog("TRACE2", "ParseNormalSubString - END");
			} else {
				api.DebugLog("ERROR", "ParseNormalSubString - Unbalanced Argument Nesting");
			}
		}
		api.DebugLog("TRACE", "ParseSubString - END");

		return SplitSearchString(SearchString, i);
	}

	private static C_SplitString SplitSearchString(String SearchString, int i)
	{
		C_SplitString SearchStruct = new C_SplitString();

		if (i != SearchString.length() - 1 ){
			SearchStruct.Prefix = SearchString.substring(0, (i-1)).trim();
			SearchStruct.Suffix = SearchString.substring(i + 1).trim();
			SearchStruct.Separator = SearchString.charAt(i);
		} else {
			SearchStruct.Prefix = SearchString.trim();
			SearchStruct.Suffix = "";
			SearchStruct.Separator = ' ';
		}

		if (SearchStruct.Prefix.startsWith("<") && SearchStruct.Prefix.endsWith(">") ){
			SearchStruct.Prefix = SearchStruct.Prefix.substring(1, SearchStruct.Prefix.length() - 1).trim();
		}

		if (!SearchStruct.Suffix.isEmpty()) {
			api.DebugLog("TRACE", "ParseSubstring - Num Args: 2");
			api.DebugLog("TRACE", "ParseSubstring - Prefix: \"" + SearchStruct.Prefix + "\"");
			api.DebugLog("TRACE", "ParseSubstring - Operator: \"" + SearchStruct.Separator + "\"");
			api.DebugLog("TRACE", "ParseSubstring - Suffix: \"" + SearchStruct.Suffix + "\"");
		} else {
			api.DebugLog("TRACE", "ParseSubstring - Num Args: 1");
			api.DebugLog("TRACE", "ParseSubstring - Prefix: \"" + SearchStruct.Prefix + "\"");
		}

		return SearchStruct;

	}

	private static C_SearchArg ParseArg(String SearchString)
	{
		C_SearchArg Op = new C_SearchArg();
		String BaseSearchString;

		api.DebugLog("TRACE2", "ParseArg - BEGIN");
		api.DebugLog("INFO", "ParseArg - Input: \"" + SearchString + "\"");


		if (SearchString.startsWith("!")) {
			Op.MatchedPasses = false;
			BaseSearchString = SearchString.substring(1).trim();
		} else {
			Op.MatchedPasses = true;
			BaseSearchString = SearchString;
		}

		String ArgExpression = null;
		int ArgOperator = BaseSearchString.indexOf('=');


		if (ArgOperator != -1) {
			ArgExpression = BaseSearchString.substring(ArgOperator + 1).replace("'", "").trim();
			BaseSearchString = BaseSearchString.substring(0, ArgOperator).trim();
		}


		if (BaseSearchString.equals("IsVideo")) {
			Op.Type = "BoolMethod";
			Op.Method = "IsVideoFile";
		} else if (BaseSearchString.equals("IsTV")) {
			Op.Type = "BoolMethod";
			Op.Method = "IsTVFile";
		} else if (BaseSearchString.equals("IsMovie")) {
			Op.Type = "BoolMethod";
			Op.Method = "ortus_api_MediaIsMovie";
		} else if (BaseSearchString.equals("IsSeries")) {
			Op.Type = "BoolMethod";
			Op.Method = "ortus_api_MediaIsSeries";
		} else if (BaseSearchString.equals("IsDVD")) {
			Op.Type = "BoolMethod";
			Op.Method = "ortus_api_IsDVD";
		} else if (BaseSearchString.equals("IsBD")) {
			Op.Type = "BoolMethod";
			Op.Method = "IsBluRay";
		} else if (BaseSearchString.equals("IsMusic")) {
			Op.Type = "BoolMethod";
			Op.Method = "IsMusicFile";
		} else if (BaseSearchString.equals("IsImportedTV")) {
			Op.Type = "BoolMethod";
			Op.Method = "ortus_api_IsMediaTypeTV";
		} else if (BaseSearchString.equals("IsArchived")) {
			Op.Type = "BoolMethod";
			Op.Method = "IsLibraryFile";
		} else if (sf.GetFilter(BaseSearchString).equals("BoolMethod")) {
			api.DebugLog("TRACE", "ParseArg - Found valid sage filter: " + BaseSearchString + " using method: " + sf.GetFilter(BaseSearchString));
			Op.Type = "BoolMethod";
			Op.Method = BaseSearchString;
		} else if (sf.GetFilter(BaseSearchString).equals("MethodRegex")) {
			api.DebugLog("TRACE", "Found valid sage filter: " + BaseSearchString + " using method: " + sf.GetFilter(BaseSearchString));
			Op.Type = "MethodRegex";
			Op.Method = BaseSearchString;
			Op.RegexPattern = Pattern.compile(ArgExpression, 2);
			Op.CompleteMatch = false;
		} else if (ValidateFilter(BaseSearchString)) {
			String[] Filt = BaseSearchString.split("_");
			if (Filt[2].startsWith("Is")) {
				Op.Type = "BoolMethod";
				Op.Method = BaseSearchString;
			} else if (Filt[2].startsWith("Get")) {
				Op.Type = "MethodRegex";
				Op.Method = BaseSearchString;
				Op.RegexPattern = Pattern.compile(ArgExpression, 2);
				Op.CompleteMatch = false;
			} else {
				Op.Type = "";
				Op.Method = "";
			}
		} else {
			Op.Type = "";
			Op.Method = "";
		}

		if (!Op.Type.isEmpty()) {
			api.DebugLog("INFO", "ParseArg - ArgType: " + Op.Type);
			api.DebugLog("INFO", "ParseArg - Method: " + Op.Method);
			if (Op.Type.equals("Method")) {
				api.DebugLog("INFO", "ParseArg - Match Value: \"" + Op.MatchValue + "\"");
			}
			if (Op.Type.equals("MethodRegex")) {
				api.DebugLog("INFO", "ParseArg - Regexp Pattern: \"" + Op.RegexPattern + "\"");
			}
			if (Op.Type.equals("MethodRegex")) {
				api.DebugLog("INFO", "ParseArg - Complete Match: " + Op.CompleteMatch);
			}
			if (Op.Type.equals("Range")) {
				api.DebugLog("INFO", "ParseArg - Lower Bound Inclusive: " + Op.LowerBoundInclusive);
			}
			if (Op.Type.equals("Range")) {
				api.DebugLog("INFO", "ParseArg - Upper Bound Exclusive: " + Op.UpperBoundExclusive);
			}
			api.DebugLog("INFO", "ParseArg - MatchedPasses: " + Op.MatchedPasses);
		} else {
			api.DebugLog("ERROR", "ParseArg - SearchString Not Recognized: \"" + SearchString + "\"");
		}
		api.DebugLog("TRACE2", "ParseArg - END");

		return Op;
	}

    private static Object[] EvalArg(Object[] SourceList, C_SearchArg Op)
    {
		api.DebugLog("TRACE2", "EvalArg - BEGIN");
        long t0 = System.currentTimeMillis();

		Object[] OutputList = null;

		if (Op.Type.equals("BoolMethod")){
            OutputList = (Object[])Database.FilterByBoolMethod(SourceList, Op.Method, Op.MatchedPasses);
        }else if (Op.Type.equals("Method")){
            OutputList = (Object[])Database.FilterByMethod(SourceList, Op.Method, Op.MatchValue, Op.MatchedPasses);
        }else if (Op.Type.equals("MethodRegex")){
            OutputList = (Object[])Database.FilterByMethodRegex(SourceList, Op.Method, Op.RegexPattern, Op.MatchedPasses, Op.CompleteMatch);
        }else if (Op.Type.equals("Range")){
            OutputList = (Object[])Database.FilterByRange(SourceList, Op.Method, Op.LowerBoundInclusive, Op.UpperBoundExclusive, Op.MatchedPasses);
        }else{
            OutputList = null;
        }

        long t1 = System.currentTimeMillis() - t0;
		api.DebugLog("TRACE","EvalArg - OutputList Size = " + OutputList.length );
        api.DebugLog("INFO","EvalArg - Method: " + Op.Method + ", Execution Time = " + t1 + " ms" );
		api.DebugLog("TRACE2", "EvalArg - END");

		return OutputList;
    }

	private static Object EvalReturnOpts(Object Source, C_ReturnOpts RetOpts)
	{
		
		//heirarchal list of keys.  Optional Return Operations will execute from beginning to end independent of how they are defined in the SearchString
		String[] Keys = {"Sort","SortLexical","SortByCustomMethod","MaxObjects","GroupByMethod","GroupByArrayMethod","GroupByCustomMethod","MaxGroups"};
		long t0 = 0;
		long t1 = 0;


		if (RetOpts.OptsExist){
			Object Output = Source;
			boolean Reverse = false;
			String BaseValue = "";
			int j = 0;

			for (int i=0; i<Keys.length; i++){
				j = 0;
				while (!RetOpts.OptsArray[j][0].equals(Keys[i])){
					j++;
					if (j>=RetOpts.OptsArray.length){
						break;
					}
				}
				if (j>=RetOpts.OptsArray.length){
					api.DebugLog("TRACE2", "EvalReturnOpts - Key Not Found: \"" + Keys[i] + "\"");
				} else {
					if (RetOpts.OptsArray[j][1].startsWith("!")){
						Reverse = true;
						BaseValue = RetOpts.OptsArray[j][1].substring(1).trim();
					} else {
						Reverse = false;
						BaseValue = RetOpts.OptsArray[j][1].trim();
					}
					api.DebugLog("TRACE", "EvalReturnOpts - Key Found: \"" + RetOpts.OptsArray[j][0] + "\"");
					api.DebugLog("TRACE", "EvalReturnOpts - Value: \"" + BaseValue + "\"");
					api.DebugLog("TRACE", "EvalReturnOpts - Reverse: " + Reverse);

					t0 = System.currentTimeMillis();

					if (Keys[i].equals("Sort")){
						Output = Database.Sort(Output, Reverse, BaseValue);
					} else if (Keys[i].equals("SortLexical")){
						Output = Database.SortLexical(Output, Reverse, BaseValue);
					} else if (Keys[i].equals("SortByCustomMethod")){
						//INSERT CUSTOM METHOD FUNCTION CALL HERE...
						//Output = custom method call reflection...
					} else if (Keys[i].equals("MaxObjects")){
						Output = phoenix.api.SubArray(Output, 0, Integer.parseInt(BaseValue));
					} else if (Keys[i].equals("GroupByMethod")){
						Output = Database.GroupByMethod(Output, BaseValue);
					} else if (Keys[i].equals("GroupByArrayMethod")){
						Output = Database.GroupByArrayMethod(Output, BaseValue);
					} else if (Keys[i].equals("GroupByCustomMethod")){
 						//INSERT CUSTOM METHOD FUNCTION CALL HERE...
						//Output = custom method call reflection...
					} else if (Keys[i].equals("MaxGroups")){
						//INSERT METHOD HERE TO RETURN ONLY X GROUPS...
						//Output = GROUP TRIMMING METHOD...
					}else {
						api.DebugLog("WARNING", "EvalReturnOpts - Unknown Key | Value: \"" + RetOpts.OptsArray[j][0] + "\" | \"" + RetOpts.OptsArray[j][1] + "\"");
					}
					t1 = System.currentTimeMillis() - t0;
					api.DebugLog("TRACE", "EvalReturnOpts - Key execution time = " + t1 + " ms");
				}
			}
			return Output;
		} else{
			return Source;
		}
	}

    private static boolean ValidateFilter(String Filter)
    {
        boolean Result = false;
        String[] filterclass = Filter.split("_");
        try {
            Object[] x = Class.forName(filterclass[0] + "." + filterclass[1]).getMethods();
            for ( Object y : x ) {
                Method z = (Method) y;
                String mn = z.getName();
                if ( mn.equals(filterclass[2])) {
                   api.DebugLog("TRACE", "Custom Filter Found:  " + filterclass[0] + "." +  filterclass[1]+ "." + mn );
                   Result = true;
                   break;
                }
            }
         } catch (Exception e) {
           api.DebugLog("ERROR", "ValiateFilter - Custom Filter Exception:  " + e );
           return Result;
         }

        return Result;
    }
}