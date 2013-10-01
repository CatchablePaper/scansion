/*Latin: Dactylic Hexameter Scansion Software
 * Version: 1.0
 * Author: Johnny Tang
 * Date: October 1, 2013
 * Content: scansion of dactylic hexameter lines in the Aeneid
 * License: MIT License
 */

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class scansion {

	static ArrayList<ArrayList<String>> scansion = new ArrayList();

	public static void bruteSearch (ArrayList<String> longshort, ArrayList<Integer> unknownsPos)
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

	public static boolean flexibleEqual (boolean[] array1, String[] array2)
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

	public static void main (String args[]) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Line: ");
		/*ArrayList<String> input = new ArrayList();
		ArrayList<String> output = new ArrayList();
		String inline;
		while (!((inline = br.readLine()).equals("Go!")))
		{
			input.add(inline);
			System.out.println(input);
		}
		 */
		String line = br.readLine();
		System.out.println("This Line is "+line);
		String[] vowels = {"a", "A", "e", "E", "i", "I", "o", "O", "u", "U"};
		String[] diphthongs = {"ae", "au", "ei", "eu", "oe", "ui"};
		ArrayList<Integer> vowelPositions = new ArrayList();
		ArrayList<int[]> vowelGroups = new ArrayList();
		ArrayList<String> longshort = new ArrayList();
		ArrayList<Integer> alreadyLong = new ArrayList();
		ArrayList<ArrayList<String>> plausible = new ArrayList();
		ArrayList<String> finalLongShort = new ArrayList();
		System.out.println("Pre-Plausible: "+plausible.size());

		//Remove Punctuation
		line = line.replaceAll("[^ A-Za-z0-9()]", "");
		
		//U and I Checks:
		line = line.replaceAll("iactatus", "jactatus");
		line = line.replaceAll("uisa","visa");
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
			//prepare this vowel for adding
			int[] thisVowel = {vowelPositions.get(i-1)};

			//Exception 1: special macrons
			int prevSpace = originalLine.substring(0,thisVowel[0]).lastIndexOf(" ")+1;
			int nextSpace = originalLine.substring(thisVowel[0],originalLine.length()).indexOf(" ")+thisVowel[0];
			String thisWord = "";
			System.out.println("For Vowel at "+thisVowel[0]+", previous space = "+prevSpace+" at "+originalLine.substring(0,thisVowel[0])+", next space = "+nextSpace+" at "+originalLine.substring(thisVowel[0],originalLine.length()));

			try
			{
				thisWord = ""+originalLine.substring(prevSpace,nextSpace);
				thisWord = thisWord.replaceAll("[^A-Za-z0-9]", "");;
				//System.out.println(thisWord);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				thisWord = "herp";
			}

			//Exceptions
			String[] deus = {"dei", "deo", "di", "dii", "deorum", "dis", "diis", "deis", "deos", "deum"};
			String[] latium = {"Latio"};
			String[] pietate = {"pietate"};
			String[] italia = {"Italiam"};
			String[] moenia = {"moenia"};
			System.out.println("This word = "+thisWord);
			if (Arrays.asList(deus).contains(thisWord))
			{
				System.out.println("Deus"+vowelPositions.get(i-1));
				int[] prevVowel = {line.indexOf(thisWord)+2};
				int[] nextVowel = {line.indexOf(thisWord)+3};
				System.out.println(prevVowel[0]+" "+nextVowel[0]);
				vowelGroups.add(prevVowel);
				vowelGroups.add(nextVowel);
				alreadyLong.add(vowelGroups.size());
				System.out.println(vowelGroups.size());
				System.out.println("already long: "+vowelGroups.size());
				i++;
			}		
			//Exception - Latio
			else if (Arrays.asList(latium).contains(thisWord))
			{
				System.out.println("Latio");
				int[] A = {vowelPositions.get(i-1)};
				int[] I = {vowelPositions.get(i-1)+2};
				int[] O = {vowelPositions.get(i-1)+3};
				vowelGroups.add(A);
				vowelGroups.add(I);
				vowelGroups.add(O);
				alreadyLong.add(vowelGroups.size());
				i = i+2;
			}
			//Exception - Pietate
			else if(Arrays.asList(pietate).contains(thisWord))
			{
				int[] I = {vowelPositions.get(i-1)};
				int[] E = {vowelPositions.get(i-1)+1};
				int[] A = {vowelPositions.get(i-1)+3};
				int[] E2 = {vowelPositions.get(i-1)+5};
				vowelGroups.add(I);
				vowelGroups.add(E);
				vowelGroups.add(A);
				vowelGroups.add(E2);
				i=i+3;
			}
			//Exception - Italia
			else if (Arrays.asList(italia).contains(thisWord))
			{
				int[] I = {vowelPositions.get(i-1)};
				int[] A = {vowelPositions.get(i-1)+2};
				int[] I2 = {vowelPositions.get(i-1)+4};
				int[] A2 = {vowelPositions.get(i-1)+5};
				vowelGroups.add(I);
				vowelGroups.add(A);
				vowelGroups.add(I2);
				vowelGroups.add(A2);
				alreadyLong.add(vowelGroups.size());
				i=i+3;
			}
			//Exception - Moenia
			else if (Arrays.asList(moenia).contains(thisWord))
			{
				System.out.println("Moenia: "+line.indexOf(thisWord));
				//int[] OE = {line.indexOf(thisWord)+2,line.indexOf(thisWord)+3};
				int[] I = {line.indexOf(thisWord)+5};
				int[] A = {line.indexOf(thisWord)+6};
				//vowelGroups.add(OE);
				vowelGroups.add(I);
				vowelGroups.add(A);
				i++;
			}


			//Case 1: no previous adjacent vowel
			else if ((i == 1) || (vowelPositions.get(i-1)-vowelPositions.get(i-2) != 1))
			{
				vowelGroups.add(thisVowel);
			}

			//Case 2: yes previous adjacent vowel
			else
			{
				//search through all int[] in vowelGroups, and find the previous vowel position in a particular int[]
				//then check to see if that particular int[] has two elements (i.e., end of vowel-pair) or one element (not end)
				int[] prev = vowelGroups.get(vowelGroups.size()-1);

				//Subcase 1: previous adjacent vowel is end of a vowel-pair
				//System.out.println(prev.length);
				if (prev.length == 2)
				{
					//System.out.println("Vowel!!!");
					vowelGroups.add(thisVowel);
				}
				//Subcase 2: previous adjacent vowel is not end of a vowel-pair
				else if (prev.length == 1)
				{
					//System.out.println("Vowel!!!");
					int[] both = {prev[0],thisVowel[0]};
					vowelGroups.set(vowelGroups.size()-1, both);
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
				String nextTwo = originalLine.charAt(lastPosThis+1)+""+originalLine.charAt(lastPosThis+2);
				if ((!nextTwo.equals("tr")) && (firstPosNext - lastPosThis > 2) && (originalLine.charAt(lastPosThis)!=originalLine.charAt(lastPosThis+1)))
				{
					System.out.println("Pairing: "+(line.charAt(lastPosThis+1))+","+line.charAt(lastPosThis+2));
					System.out.println("2 Consonants "+j);
					longshort.set(j-1, "ConsonantLong");
					continue; //go to next vowel group
				}
			}

			//Long Vowel #2: vowel group is a diphthong
			int[] thisVowelGroup = vowelGroups.get(j-1);
			if (thisVowelGroup.length == 2 && longshort.get(j-1).equals("Unknown"))
			{
				System.out.println(thisVowelGroup[0]);
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
				System.out.print(s);
			}
			System.out.println();

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


		//output.add(thisPossible);
		//thisPossible = null;
		//plausible.clear();
		//System.out.println(alreadyLong.get(0));

		//output all lines
		/*for (int i = 1; i <= output.size(); i++)
	{
		System.out.println(input.get(i-1)+" SCANNED AS: \n"+output.get(i-1));
	}*/
		//Marking on Top
		/*String top = "";
		int vowelCounter = 0;
		elided = elided.replaceAll("\\s","");
		elided = elided.replaceAll("[^A-Za-z0-9()]", "");
		for (int i = 1; i <= elided.length(); i++)
		{
			//means this is a vowel
			if ((i < elided.length()) && (elided.charAt(i) == '(' || elided.charAt(i) == ')'))
			{
				top = top + " ";
			}
			else if ((vowelCounter != vowelGroups.size()) && (i == vowelGroups.get(vowelCounter)[0]))
			{
				if (finalLongShort.get(vowelCounter).equals("Long"))
				{
					top = top + "-";
					vowelCounter++;
				}
				else
				{
					top = top + "u";
					vowelCounter++;
				}

			}
			else
			{
				top = top + " ";
			}
		}*/
		System.out.println("****************************");
		System.out.println("RESULTS");
		System.out.println("****************************");
		//System.out.println(top);
		System.out.println(elided);

		//iterate out all plausible scansions
		String thisPossible = "";
		//System.out.println(plausible.size());
		int SIZE = plausible.size();
		for (int k = 1; k <= 1; k++)
		{
			thisPossible += k+". ";
			ArrayList<String> currentPlausible = plausible.get(0);
			for (String DS : currentPlausible)
			{
				thisPossible += DS+" ";
				System.out.print(DS+" ");
			}//end of the for loop
			System.out.println();
			thisPossible += "\n";
			//System.out.println("This Possible: "+thisPossible);
			plausible.remove(plausible.size()-1);
		}


	}
}
