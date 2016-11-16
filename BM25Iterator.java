/*  BM25ITERATOR SUMMARY

	This ranking algorithm is an implementation which will expand retrieval
	from simple word-based filtering. The BM25 iterator also incorporates
	information structures such as headings, titles, and dates of creation,
	as well as word relationships based on recurrence rates of words.

    Examples where original ranking algorithm is superior:

        1.	Search for "weather report"
			Original algorithm is superior here because:

			This ranking gives me documents more relevant to actual weather,
			such as weather stations, because they are a very specific area
			which deals with the weather. BM25 gives results, which deals
			with broader ecological events.

		2.	Search for "law"
			Original algorithm is superior here because:

			In this search I am, supposedly, only looking to read up
			information on law, not on specific laws. This algorithm provides
			more broad documents regarding law, whereas BM25 provides a more
			specific selection, such as specific laws and policies.

    Examples where BM25Iterator algorithm is superior:

		1.	Search for "money"
			BM25 is superior here because:

			the original ranking algorithm is biased towards the relationship
			between the overall length of the document and the frequency
			occurence of the query in that document. Therefore, searching
			"money" in the original algorithm retrieved a short, irrelevant
			document, but it was rated highly because it was dense with the
			query compared to its size. BM25 is not so dependent on the
			term frequency in the resulting documents.

		2.	Search for "best books"
			BM25 is superior here because:

			when using the "combine" and "feature" tags in our BM25 query
			we are given a broader selection of links which are weighted
			by the given query. I think the BM25 provides a clearer
			selection of documents which may contain lists of best books,
			while the original ranking simply gives me writers with
			many awards.
*/

package org.galagosearch.exercises;

import java.io.IOException;
import org.galagosearch.core.retrieval.structured.CountIterator;
import org.galagosearch.core.retrieval.structured.RequiredStatistics;
import org.galagosearch.core.retrieval.structured.ScoringFunctionIterator;
import org.galagosearch.tupleflow.Parameters;

/**
 * @author roman
 */

@RequiredStatistics(statistics = {"collectionLength", "documentCount"})

public class BM25Iterator extends ScoringFunctionIterator {

	private double k1;
	private double k2;
	private double b;

	private double docFreq;
	private double docCount;
	private double colLength;
	private double avgDocLength;

	public BM25Iterator(Parameters parameters, CountIterator iterator) throws IOException {

		super(iterator);

		k1 			= Double.parseDouble(parameters.get("k_1", "1.2"));
		k2 			= Double.parseDouble(parameters.get("k_2", "100"));
		b 			= Double.parseDouble(parameters.get("b", "0.75"));

		docFreq 	= 0;
		docCount 	= Double.parseDouble(parameters.get("documentCount", "100000"));
		colLength 	= Double.parseDouble(parameters.get("collectionLength", "80000"));
		avgDocLength = colLength / docCount;

        while (!iterator.isDone()) {

            docFreq += 1;
            iterator.nextDocument();
		}

		iterator.reset();
	}

	/**
	 * @param count equals to fi
	 * @param length equals to dl
	 *
	 * Ignores ( (k2+1)*qfi ) / (k2+qfi) because the performance is less sensitive
	 * Only calculate ( (k1+1)*fi ) / (K + fi)
	 */

	@Override
	public double scoreCount(int count, int length) {

		double K 		= k1 * ((1 - b) + b * length);

		double top1 	= docCount - docFreq + 0.5;
		double bottom1 	= docFreq + 0.5;

		double top2 	= (k1 + 1) * count;
		double bottom2 	= K + count;

		double res1 	= top1 / bottom1;
		double res2 	= top2 / bottom2;

		return Math.log(res1) * res2;
	}
}
