/*
 *  Copyright (c) 2009 Ondrej Dusek
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without modification, 
 *  are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this list 
 *  of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice, this 
 *  list of conditions and the following disclaimer in the documentation and/or other 
 *  materials provided with the distribution.
 *  Neither the name of Ondrej Dusek nor the names of their contributors may be
 *  used to endorse or promote products derived from this software without specific 
 *  prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 *  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 *  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 *  OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * @file Main.java The main class, reading command arguments and input.
 * @author OndÅ™ej DuÅ¡ek
 */

package org.lingutil.bleu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The main class, reading command arguments and input.
 */
public class Main
{
	// wk
	private static String seg_bing = "The present instruction manual provides the necessary information for installation, connection, commissioning and important notes for maintenance and troubleshooting. Therefore read this before and keep on them as a product available at any time in the immediate vicinity of the device.";
	@SuppressWarnings("unused")
	private static String seg_de = "Die vorliegende Betriebsanleitung liefert Ihnen die erforderlichen Informationen für Montage, Anschluss und Inbetriebnahme sowie wichtige Hinweise für Wartung und Störungsbeseitigung. Lesen Sie diese deshalb vor der Inbetriebnahme und bewahren Sie sie als Produktbestandteil in unmittelbarer Nähe des Gerätes jederzeit zugänglich auf.";
	private static String seg_google = "This operating instructions manual provides all the information you need for mounting, connection and setup as well as important instructions for maintenance and fault rectification. Read this manual before you start and to keep it as accessible in the immediate vicinity of the device at all times.";
	private static String seg_translator = "This operating instructions manual provides all the information you need for mounting, connection and setup as well as important instructions for maintenance and fault rectification. Please read this information before putting the instrument into operation and keep this manual accessible in the immediate vicinity of the device.";

	/**
	 * @param args
	 *            the command line arguments
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		BleuMeasurer bm;
		bm = new BleuMeasurer();
		String candLine = null, refLine = null;
		String[] candTokens;
		String[] refTokens;
		candLine = seg_google;
		refLine = seg_translator;
		candLine.trim();
		refLine.trim();
		candTokens = candLine.split("\\s+");
		refTokens = refLine.split("\\s+");

		// add sentence to stats
		bm.addSentence(refTokens, candTokens);
		
		int val = de.folt.similarity.LevenshteinSimilarity.levenshteinSimilarity(candLine, refLine, 30);
		
		System.out.println("BLEU score Google: " + bm.bleu() + " Levenshtein: " + val);
		candLine = seg_bing;
		candLine.trim();
		refLine.trim();
		candTokens = candLine.split("\\s+");
		refTokens = refLine.split("\\s+");

		// add sentence to stats
		bm.addSentence(refTokens, candTokens);
		val = de.folt.similarity.LevenshteinSimilarity.levenshteinSimilarity(candLine, refLine, 30);
		System.out.println("BLEU score Bing:   " + bm.bleu() + " Levenshtein: " + val);
		
		candLine = "Hello here I am and you are there";
		refLine = "Hello me am here and you are there";
		candLine.trim();
		refLine.trim();
		candTokens = candLine.split("\\s+");
		refTokens = refLine.split("\\s+");

		// add sentence to stats
		bm = new BleuMeasurer(candLine, refLine);
		System.out.println("BLEU score test 1:   " + bm.bleu() + " Levenshtein: " + val);
		
		bm.addSentence(refTokens, candTokens);
		System.out.println("BLEU score test 2:   " + bm.bleu() + " Levenshtein: " + val);
		
		if (1 == 2)
		{
			// BleuMeasurer bm;
			BufferedReader inRef = null, inCand = null;
			boolean eof = false;
			int lineCtr = 0;

			// parameters check
			if (args.length != 2)
			{
				System.err.println("Input parameters: reference_file candidate_file");
				System.exit(1);
			}

			// initialization, opening the files
			bm = new BleuMeasurer();

			try
			{
				inRef = new BufferedReader(new FileReader(args[0]));
				inCand = new BufferedReader(new FileReader(args[1]));
			}
			catch (FileNotFoundException e)
			{
				System.err.println(e.getMessage());
				System.exit(2);
			}

			// read sentence by sentence
			while (!eof)
			{

				// String candLine = null, refLine = null;
				// String[] candTokens;
				// String[] refTokens;

				try
				{
					refLine = inRef.readLine();
					candLine = inCand.readLine();
				}
				catch (IOException ex)
				{
					System.err.println(ex.getMessage());
					System.exit(1);
				}

				// test for EOF
				if (candLine == null && refLine == null)
				{
					break;
				}
				if (candLine == null || refLine == null)
				{
					System.err.println("The files are of different lengths.");
					System.exit(1);
				}

				// split to tokens by whitespace
				candLine.trim();
				refLine.trim();
				candTokens = candLine.split("\\s+");
				refTokens = refLine.split("\\s+");

				// add sentence to stats
				bm.addSentence(refTokens, candTokens);
				if (lineCtr % 100 == 0)
				{
					System.err.print(".");
				}
				lineCtr++;
			}

			// print the result
			System.err.println("Total:" + lineCtr + " sentences.");
			System.out.println("BLEU score: " + bm.bleu());
		}
	}

}
