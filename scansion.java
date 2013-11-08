/*Latin: Dactylic Hexameter Scansion Software
 * Version: 3.0
 * Author: Johnny Tang
 * Date: November 8, 2013
 * Content: scansion of dactylic hexameter lines in the Aeneid
 * License: MIT License
 */

import java.lang.*;
import java.util.*;
import java.io.*;

import javax.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

class Lines {

	ArrayList<ArrayList<String>> scansion = new ArrayList();
	String LINE;

	public void bruteSearch (ArrayList<String> longshort, ArrayList<Integer> unknownsPos)
	{
		//Duplicate new longshort and unknownsPos
		ArrayList<String> thisLongShort = new ArrayList();
		ArrayList<Integer >thisUnknownsPos = new ArrayList();

		for (String s : longshort)
		{
			thisLongShort.add(s);
		}

		for (Integer t : unknownsPos)
		{
			thisUnknownsPos.add(t);
		}

		//Terminating Condition
		if (thisUnknownsPos.size() == 0)
		{
			String output = "";
			for (String s : thisLongShort)
			{
				output = output + " " + s;
			}
			//System.out.println("One Possibility: "+output);
			scansion.add(thisLongShort);

		}

		//Recursive Steps
		else
		{
			//System.out.println(thisUnknownsPos.size());
			int currentPosition = thisUnknownsPos.get(0);
			thisUnknownsPos.remove(0);

			//copy thisLongShort again
			ArrayList<String> shortLongShort = new ArrayList();
			for (String s : thisLongShort)
			{
				shortLongShort.add(s);
			}

			//Try Long
			thisLongShort.set(currentPosition, "Long");
			bruteSearch(thisLongShort, thisUnknownsPos);

			//Try Short
			shortLongShort.set(currentPosition, "Short");
			bruteSearch(shortLongShort, thisUnknownsPos);
		}

	}

	public boolean flexibleEqual (boolean[] array1, String[] array2)
	{
		boolean equal = true;
		for (int i = 0; i < array2.length; i++)
		{
			String one = "";
			if (array1[i])
			{
				one = "true";
			}
			else
			{
				one = "false";
			}

			if (!(array2[i].equals("?") || one.equals(array2[i])))
			{
				equal = false;
			}
		}
		return equal;
	}

	public int countOccurrences(String haystack, String needle, int i){
		return ((i=haystack.indexOf(needle, i)) == -1)?0:1+countOccurrences(haystack, needle, i+1);}

	public String[] scanLine(String line)
	{
		System.out.println("This Line is "+line);
		String[] vowels = {"a", "A", "e", "E", "i", "I", "o", "O", "u", "U"};
		String[] diphthongs = {"ae", "au", "ei", "eu", "oe"};
		String[] vowelpair = {"ui", "oi", "uo", "ue", "ua"};
		ArrayList<Integer> vowelPositions = new ArrayList();
		ArrayList<int[]> vowelGroups = new ArrayList();
		ArrayList<String> longshort = new ArrayList();
		ArrayList<Integer> alreadyLong = new ArrayList();
		ArrayList<ArrayList<String>> plausible = new ArrayList();
		ArrayList<String> finalLongShort = new ArrayList();
		System.out.println("Pre-Plausible: "+plausible.size());

		line = line.replaceAll("[^ A-Za-z0-9()]", "");
		line = line.toLowerCase();
		
		String elided = line;
		System.out.println("This Line is Now: "+line);

		//Elision Checks:
		for (int i = 3; i <= line.length()-2; i++)
		{
			//1. Find Space
			if (line.charAt(i-1) == ' ')
			{
				//2. Get Three Chars to the Left of the Space (if space at char 3 e.g. "et " then get two chars
				boolean prev3CharVowel = false;
				if (i != 3)
				{
					prev3CharVowel = Arrays.asList(vowels).contains(""+line.charAt(i-4));
				}
				else
				{
					prev3CharVowel = false;
				}
				boolean prev2CharVowel = Arrays.asList(vowels).contains(""+line.charAt(i-3));
				boolean prev1CharVowel = Arrays.asList(vowels).contains(""+line.charAt(i-2));
				boolean prev1CharM = ((""+line.charAt(i-2)).equals("m"));
				//3. Get Three Chars to the Right of the Space (if space at char 3 e.g. " et" then get two chars
				boolean next3CharVowel = false;
				if (i != 3)
				{
					System.out.println(i);
					next3CharVowel = Arrays.asList(vowels).contains(""+line.charAt(i+2));
				}
				else
				{
					next3CharVowel = false;
				}
				boolean next2CharVowel = Arrays.asList(vowels).contains(""+line.charAt(i+1));
				boolean next1CharVowel = Arrays.asList(vowels).contains(""+line.charAt(i));
				boolean next1CharH = ((""+line.charAt(i)).equals("h"));

				System.out.println(line.charAt(i-2)+" "+line.charAt(i));

				//Determinant:
				//[0] = prev3CharVowel		//[4] = next1CharH
				//[1] = prev2CharVowel		//[5] = next1CharVowel
				//[2] = prev1CharVowel		//[6] = next2CharVowel
				//[3] = prev1CharM			//[7] = next3CharVowel
				boolean[] determinant = {prev3CharVowel,prev2CharVowel,prev1CharVowel,prev1CharM,next1CharH,next1CharVowel,next2CharVowel,next3CharVowel};
				System.out.println(Arrays.toString(determinant));

				boolean[] Case1 = {true,true,false,true,true,false,true,true};
				boolean[] Case2 = {true,true,false,true,true,false,true,false};
				boolean[] Case3 = {false,true,false,true,true,false,true,true};
				boolean[] Case4 = {false,true,false,true,true,false,true,false};

				//Case #1: "AAm hAA": determinant = {T,T,F,T,T,F,T,T}
				if (Arrays.equals(determinant, Case1))
				{
					//diphthong or no diphthong
					String prevGroup = prev3CharVowel+""+prev2CharVowel;
					String nextGroup = next2CharVowel+""+next3CharVowel;
					if (Arrays.asList(diphthongs).contains(prevGroup))
					{
						line = line.substring(0,i)+line.substring(i+3);
						//modify elided
						elided = elided.substring(0,i)+"("+elided.substring(i,i+3)+")"+elided.substring(i+3);
					}
					else
					{
						line = line.substring(0,i-4)+line.substring(i-1);
						elided = elided.substring(0,i-4)+"("+elided.substring(i-4,i-1)+")"+elided.substring(i-1);
					}
					System.out.println("Elision!");
				}
				//Case #2: "AAm hA": determinant = {T,T,F,T,T,F,T,F}
				else if (Arrays.equals(determinant,  Case2))
				{
					//diphthong or no diphthong
					String prevGroup = prev3CharVowel+""+prev2CharVowel;
					if (Arrays.asList(diphthongs).contains(prevGroup))
					{
						line = line.substring(0,i)+line.substring(i+2);
						elided = elided.substring(0,i)+"("+elided.substring(i,i+2)+")"+elided.substring(i+2);
					}
					else
					{
						line = line.substring(0,i-4)+line.substring(i-1);
						elided = elided.substring(0,i-4)+"("+elided.substring(i-4,i-1)+")"+elided.substring(i-1);
					}
					System.out.println("Elision!");
				}
				//Case #3: "Am hAA": determinant = {F,T,F,T,T,F,T,T}
				else if (Arrays.equals(determinant, Case3))
				{
					//diphthong or no diphthong: DOESN'T MATTER - always remove front
					line = line.substring(0,i-3)+line.substring(i-1);
					elided = elided.substring(0,i-3)+"("+elided.substring(i-3,i-1)+")"+elided.substring(i-1);
					System.out.println("Elision!");
				}
				//Case #4: "Am hA": determinant = {F,T,F,T,T,F,T,F}
				else if (Arrays.equals(determinant,Case4))
				{
					line = line.substring(0,i-3)+line.substring(i-1);
					elided = elided.substring(0,i-3)+"("+elided.substring(i-3,i-1)+")"+elided.substring(i-1);
					System.out.println("Elision!");
				}
				//Case #5: "AA AA": determinant = {?,T,T,F,F,T,T,?}
				String[] Case5 = {"?","true","true","false","false","true","true"};
				if (flexibleEqual(determinant,Case5))
				{
					//diphthong or no diphthong
					String prevGroup = prev2CharVowel+""+prev1CharVowel;
					String nextGroup = next1CharVowel+""+next2CharVowel;
					if (Arrays.asList(diphthongs).contains(prevGroup))
					{
						line = line.substring(0,i)+line.substring(i+2);
						elided = elided.substring(0,i)+"("+elided.substring(i,i+2)+")"+elided.substring(i+2);
					}
					else
					{
						line = line.substring(0,i-3)+line.substring(i-1);
						elided = elided.substring(0,i-3)+"("+elided.substring(i-3,i-1)+")"+elided.substring(i-1);
					}
					System.out.println("Elision!");
				}
				//Case #6: "AA A": determinant = {F,T,T,F,F,T,F,?}
				String[] Case6 = {"false","true","true","false","false","true","false","?"};
				if (flexibleEqual(determinant,Case6))
				{
					//diphthong or no diphthong: DOESN'T MATTER - always remove back
					line = line.substring(0,i)+line.substring(i+1);
					elided = elided.substring(0,i)+"("+elided.substring(i,i+1)+")"+elided.substring(i+1);
					System.out.println("Elision!");
				}
				//Case #7: "A AA": determinant = {?,F,T,F,F,T,T,?}
				String[] Case7 = {"?","false","true","false","false","true","true","?"};
				if (flexibleEqual(determinant,Case7))
				{
					//diphthong or no diphthong: DOESN'T MATTER - always remove front
					line = line.substring(0,i-2)+line.substring(i-1);
					elided = elided.substring(0,i-2)+"("+elided.substring(i-2,i-1)+")"+elided.substring(i-1);
					System.out.println("Elision!");
				}
				//Case #8: "A A": determinant = {?,F,T,F,F,T,F,?}
				String[] Case8 = {"?","false","true","false","false","true","false","?"};
				if (flexibleEqual(determinant,Case8))
				{
					line = line.substring(0,i-2)+line.substring(i-1);
					elided = elided.substring(0,i-2)+"("+elided.substring(i-2,i-1)+")"+elided.substring(i-1);
					System.out.println("Elision!");
				}
				//Case #9: "Am A": determinant = {?,T,F,T,F,T,?,?}
				String[] Case9 = {"?","true","false","true","false","true","?","?"};
				if (flexibleEqual(determinant,Case9))
				{
					line = line.substring(0,i-3)+line.substring(i-1);
					elided = elided.substring(0,i-3)+"("+elided.substring(i-3,i-1)+")"+elided.substring(i-1);
					System.out.println("Elision");
				}
			}
		}

		//now we can remove all spaces between words
		String originalLine = ""+line;
		line = line.replaceAll("\\s","");
		line = line.replaceAll("[^A-Za-z0-9]", "");

		//Trim and Untrim Positions Match-Up
		//index indicates trimmed pos; value indicates untrimmed pos
		/*int[] pairs = new int[line.length()];
		String original = originalLine;
		int cumulative = 0;
		for (int i = 1; i <= line.length(); i++)
		{
			String letter = ""+line.charAt(i-1);
			pairs[i-1] = original.charAt(cumulative+original.indexOf(letter));
			original = original.substring(original.indexOf(letter)+1);
			cumulative = pairs[i-1];
		}*/

		System.out.println("Post-Elide: "+line);

		//Determine vowel positions
		for (int i = 1; i <= line.length(); i++)
		{
			String letter = Character.toString(line.charAt(i-1));
			if (Arrays.asList(vowels).contains(letter))
			{
				//System.out.println(i);
				vowelPositions.add(i);
				//longshort.add("Unknown");
			}
		}

		//Determine Vowel Groups
		System.out.println("HUM" + vowelPositions.size());
		for (int i = 1; i <= vowelPositions.size(); i++)
		{
			int[] thisVowel = {vowelPositions.get(i-1)};

			//Case 1: no previous adjacent vowel
			if ((i == 1) || (vowelPositions.get(i-1)-vowelPositions.get(i-2) != 1))
			{
				vowelGroups.add(thisVowel);
			}

			//Case 2: yes previous adjacent vowel
			else
			{
				//search through all int[] in vowelGroups, and find the previous vowel position in a particular int[]
				//then check to see if that particular int[] has two elements (i.e., end of vowel-pair) or one element (not end)
				int prevprev = vowelPositions.get(i-2);
				int[] prev = vowelGroups.get(vowelGroups.size()-1);
				String thisPair = ""+line.charAt(prev[0]-1)+line.charAt(thisVowel[0]-1);
				System.out.println("This pair: "+thisPair);
				
				//exceptions
				//String[] exceptions = {"meus","neis"};
				//String neighbor = line.charAt(thisVowel[0]-3)+thisPair+line.charAt(thisVowel[0]);
				//System.out.println("Neighbor: "+neighbor);
				
				//Subcase 1: previous adjacent vowel is end of a vowel-pair
				//System.out.println(prev.length);
				if (prev.length == 2)
				{
					//System.out.println("Vowel!!!");
					vowelGroups.add(thisVowel);
				}
				//Subcase 2: exceptions
				//else if (Arrays.asList(exceptions).contains(neighbor))
				//{
				//	vowelGroups.add(thisVowel);
				//	System.out.println("NEIS!");
				//}
				//Subcase 3: previous adjacent vowel is not end of a vowel-pair
				else if (prev.length == 1 && ( (Arrays.asList(diphthongs).contains(thisPair) || (Arrays.asList(vowelpair).contains(thisPair)))))
				{
					System.out.println("DAT TRUE");
					//System.out.println("Vowel!!!");
					int[] both = {prev[0],thisVowel[0]};
					vowelGroups.set(vowelGroups.size()-1, both);
				}
				//Subcase 3: two adjacent non-pair
				else
				{
					vowelGroups.add(thisVowel);
				}
			}
			System.out.println(i);
		}

		//Determine Long Vowels
		//first determine number of syllables and add these number of Long, Short, and Unknown in longshort
		for (int j = 1; j <= vowelGroups.size(); j++)
		{
			longshort.add("Unknown");
		}

		//cycle through each vowel group
		for (int j = 1; j <= vowelGroups.size(); j++)
		{
			//Long Vowel #0: vowel is already long
			Integer num = new Integer(j-1);
			//System.out.println(alreadyLong.get(0)+" - "+num);
			boolean found = false;
			for (int k = 1; k <= alreadyLong.size(); k++)
			{
				System.out.println("Test");
				if (alreadyLong.get(k-1).equals(num))
				{
					found = true;
				}
				else
				{
					System.out.println("NUM: "+alreadyLong.get(k-1)+" - "+num);
				}
			}
			if (found)
			{
				System.out.println("ALREADY LONG");
				longshort.set(j-2, "Long");
			}
			//Long Vowel #1: vowel followed by two consonants
			else if (j != vowelGroups.size()) //if the last vowel group then nothing follows AND if this vowel group is not in already long
			{
				//If position of last vowel in current vowel group and position of first vowel in next vowel group > 2, Then current vowel group is Long Vowel
				int lastPosThis = vowelGroups.get(j-1)[vowelGroups.get(j-1).length-1];
				int firstPosNext = vowelGroups.get(j)[0];
				//Exception: tr -> may be short
				String nextTwo = line.charAt(lastPosThis)+""+line.charAt(lastPosThis+1);
				System.out.println(line.charAt(lastPosThis-1)+" NEXT TWO: "+nextTwo);
				if ((!nextTwo.equals("tr")) && (firstPosNext - lastPosThis > 2) && (originalLine.charAt(lastPosThis)!=originalLine.charAt(lastPosThis+1)))
				{
					//System.out.println("2 Consonants "+j);
					longshort.set(j-1, "ConsonantLong");
					continue; //go to next vowel group
				}
			}

			//Long Vowel #2: vowel group is a diphthong
			int[] thisVowelGroup = vowelGroups.get(j-1);
			if (thisVowelGroup.length == 2 && longshort.get(j-1).equals("Unknown"))
			{
				//System.out.println(thisVowelGroup[0]);
				String vowelPair = line.charAt(thisVowelGroup[0]-1)+""+line.charAt(thisVowelGroup[1]-1);
				if (Arrays.asList(diphthongs).contains(vowelPair))
				{
					longshort.set(j-1,"Long");
					continue;
				}
			}
		}

		//Long Vowel #3: vowel group is in 5th foot = Dactyl; or 6th foot = Spondee
		int numGroups = vowelGroups.size();
		//6th Foot = Spondee
		longshort.set(numGroups-1,"Long");
		longshort.set(numGroups-2,"Long");
		//5th Foot = Dactyl
		longshort.set(numGroups-3,"Short");
		longshort.set(numGroups-4,"Short");
		longshort.set(numGroups-5,"Long");

		//Pre-Strategy
		for (int k = 1; k <= vowelGroups.size(); k++)
		{
			System.out.println(k+"th vowel group: "+Arrays.toString(vowelGroups.get(k-1))+longshort.get(k-1));
		}

		//Long Strategy #1: First vowel group is always long
		longshort.set(0,"Long");

		//Long Strategy #2: If vowel group is between two Longs, then the vowel group must be long
		for (int j = 1; j < vowelGroups.size()-1; j++)
		{
			String prevLongShort = longshort.get(j-1);
			String nextLongShort = longshort.get(j+1);

			if (prevLongShort.equals("Long") && nextLongShort.equals("Long"))
			{
				longshort.set(j,"Long");
				//System.out.println("Rule at "+j);
			}
		}

		//Now Brute Force Search
		//1. Determine positions of "Unknown"s
		ArrayList<Integer> unknown = new ArrayList(); 
		for (int j = 1; j < longshort.size(); j++)
		{
			if (longshort.get(j-1).equals("Unknown"))
			{
				unknown.add(j-1);
			}
		}
		//2. For each Unknown, recurse for both "Long" and "Short"
		bruteSearch(longshort,unknown);

		//Now Check if each Long/Short Sequence is plausible
		System.out.println(scansion.size());
		for (int k = 1; k <= scansion.size(); k++)
		{
			ArrayList<String> thisTrial = scansion.get(k-1);
			ArrayList<String> thisScansion = new ArrayList();
			int currentLongShort = 0;
			int currentSyllableCounter = 0;
			//System.out.println();
			//System.out.println("One possibility was tested!");
			for (String s : thisTrial)
			{
				//System.out.print(s);
			}
			//System.out.println();

			boolean failed = false;
			do
			{
				String LS1 = thisTrial.get(currentLongShort);
				String LS2 = thisTrial.get(currentLongShort+1);
				String LS3 = "";
				try
				{
					LS3 = thisTrial.get(currentLongShort+2);
				}
				catch (IndexOutOfBoundsException e)
				{
					LS3 = "herp";
				}
				System.out.println(LS1+LS2+LS3);
				//Now Determine LS1, LS2, LS3
				if (LS1.contains("Long") && LS2.contains("Long") && (!LS3.equals("herp")))
				{
					thisScansion.add("Spondee");
					currentLongShort = currentLongShort+2;
					currentSyllableCounter++;
				}
				else if (LS1.contains("Long") && LS2.equals("Short") && LS3.equals("Short"))
				{
					thisScansion.add("Dactyl");
					currentLongShort = currentLongShort+3;
					currentSyllableCounter++;
				}
				//gets to the end
				else
				{
					currentLongShort = thisTrial.size();
					currentSyllableCounter++;
					//one exception if reaching the end
					if (!LS3.equals("herp"))
					{
						failed = true;
					}
					else
					{
						thisScansion.add("Spondee");
					}
				}
				System.out.println(currentLongShort);
			} while (currentLongShort < thisTrial.size());

			//A valid dactylic hexameter line!
			if (currentSyllableCounter == 6 && (!failed))
			{
				plausible.add(thisScansion);
				for (String s : thisTrial)
				{
					finalLongShort.add(s);
				}
				System.out.println("It works!");
			}
		}

		//iterate out the vowels as Long/Short/Unknown
		for (int k = 1; k <= finalLongShort.size(); k++)
		{
			System.out.println(k+"th vowel group: "+finalLongShort.get(k-1));
		}

		System.out.println("****************************");
		System.out.println("RESULTS");
		System.out.println("****************************");
		//System.out.println(top);
		System.out.println(elided);

		//iterate out all plausible scansions
		String thisPossible = "";
		//System.out.println(plausible.size());
		int SIZE = plausible.size();
		String thisWorks = "";
		for (int k = 1; k <= 1; k++)
		{
			thisPossible += k+". ";
			boolean noScan = false;
			ArrayList<String> currentPlausible = new ArrayList();
			try
			{
				currentPlausible = plausible.get(0);
			}
			catch (IndexOutOfBoundsException e)
			{
				currentPlausible.add("No scansion available!");
				noScan = true;
			}
			for (String DS : currentPlausible)
			{
				thisPossible += DS+" ";
				System.out.print(DS+" ");
				thisWorks += DS+" ";
			}//end of the for loop
			System.out.println();
			thisPossible += "\n";
			//System.out.println("This Possible: "+thisPossible);
			if (noScan)
			{
				plausible.remove(plausible.size()-1);
			}
		}
		System.out.println("THIS WORKS: "+thisWorks);
		String[] returnValue = {thisWorks,elided};
		return returnValue;
	}
}

public class scansion {

	public static void main (String args[]) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Lines: ");
		/*ArrayList<String> input = new ArrayList();
		ArrayList<String> output = new ArrayList();
		String inline;
		while (!((inline = br.readLine()).equals("Go!")))
		{
			input.add(inline);
			System.out.println(input);
		}
		 */
		ArrayList<String> lines = new ArrayList();
		ArrayList<String> results = new ArrayList();
		String nowline = null;
		while (!(nowline = br.readLine()).equals("Go!"))
		{
			lines.add(nowline);
		}

		int numTotal = 0;
		int numScanned = 0;
		for (int g = 1; g <= lines.size(); g++)
		{
			Lines thisLine = new Lines();
			try
			{
				String[] scanned = thisLine.scanLine(lines.get(g-1));
				results.add(scanned[0]);
				lines.set(g-1, scanned[1]);
				numScanned++;
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				results.add("No scansion available!");
			}
			numTotal++;
		}

		System.out.println("****************************");
		System.out.println("CUMULATIVE RESULTS");
		System.out.println("****************************");

		int i = 0;
		BufferedWriter fw = new BufferedWriter(new FileWriter("C:/Users/JohnnyTang/Dropbox/Clubs/Classics/scansion/output.html"));
		fw.write("<html><table border=1>");
		for (String result : results)
		{
			System.out.println(lines.get(i)+"||"+result);
			fw.newLine();
			fw.write("<tr><td>"+lines.get(i)+"</td><td>"+result+"</td></tr>");
			i++;
		}
		fw.write("<b>Correctly Scanned: "+numScanned+"/"+numTotal+"</b>");
		fw.close();
	}
}
