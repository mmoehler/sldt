package com.mmoehler.sldt.compress;

/*-
 * #%L
 * dt
 * %%
 * Copyright (C) 2016 - 2020 Michael Moehler
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ObjectArrays;
import com.mmoehler.sldt.intern.Indicator;
import com.mmoehler.sldt.intern.Indicators;
import com.mmoehler.sldt.intern.Indicators.Orientation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mmoehler.sldt.intern.IndicatorSigns.*;

public final class Consolidator {

  /**
   *
   *
   * <pre>
   * 1.
   * Prüfen, in welchen Bedingungszeilen alle Bedingungsanzeiger der betreffenden Bedingung
   * mindestens je einmal auftreten. Solche Bedingungszeilen besonders kennzeichnen.
   *
   * 2.
   * Minimierungs-Analyse auf der Basis der obersten gekennzeichneten Bedingungszeile in folgenden
   * Stufen:
   *
   * - Regeln, die in dieser Zeile einen Indifferenzanzeiger aufweisen, bleiben
   *   unberücksichtigt,
   *
   * - die verbliebenen Regeln werden untereinander verglichen, wobei jedoch die
   *   Bedingungsanzeiger in der untersuchten Zeile unberücksichtigt bleiben. Wenn die Bedingung der
   *   untersuchten Zeile i Anzeiger hat, dann werden alle Gruppen aus identischen Regeln
   *   identifiziert.
   *
   * - Jede identifizierte Regelgruppe wird zu einer einzigen komplexen Regel zusammengefasst,
   *   wobei die Bedingung der untersuchten Bedingungszeile einen Indifferenzanzeiger
   *   erhält.
   *
   * - Nach der Zusammenfassung der Regelgruppen prüfen, ob immer noch alle Bedingungsanzeiger in der
   *   untersuchten Zeile explizit vorhanden sind.
   *   Wenn das der Fall ist, bleibt die Bedingungszeile gekennzeichnet, andernfalls wird das
   *   Kennzeichen gelöscht.
   *
   * 3.
   * Alle anderen gekennzeichneten Bedingungszeilen nach der unter 2. beschriebenen Prozedur behandeln.
   *
   * 4.
   * Beendigung der Minimierungsanalyse, wenn
   *  a) keine Bedingungszeile mehr gekennzeichnet ist oder
   *  b) nach dem letzten erfolgreichen Minimierungsschritt alle noch gekennzeichneten Bedingungszeilen
   *     erfolglos überprüft worden sind.
   * </pre>
   */
  public static Indicators consolidate(Indicators source) {

    // #1 Remember the number of conditions and conditions
    final int countOfConditions = source.getCountOfConditions();
    final int countOfActions = source.getCountOfActions();

    // #2 Each condition must be configured with a full set of indicators because we're handle only
    //    Limmited decisin tables these are the indicators 'Y' and 'N'. If a condition does not
    //    fulfills this requrement it is ommitted from processing
    final BitSet freeRows = indicatorsComplete(source);

    // #3 For easier access to the rules, transpose the given indicators
    //    in a column oriented matrix and group the rules by their actions.
    //    Consolidate each rule group
    final Indicator[] consolidatedIndicators =
        source
            .cols()
            .collect(
                Collectors.groupingBy(
                    (s) ->
                        (Arrays.copyOfRange(
                            s,
                            countOfConditions,
                            countOfConditions + countOfActions,
                            Indicator[].class)),
                    () ->
                        new TreeMap<>(
                            (l1, r) ->
                                Arrays.compare(l1, r, Comparator.comparingInt(Indicator::sign))),
                    Collectors.toUnmodifiableList()))
            .values()
            .stream()
            .flatMap(consolidateRuleGroup(countOfConditions))
            .reduce(new Indicator[0], (l, r) -> ObjectArrays.concat(l, r, Indicator.class));

    // #6 Transform the raw result data into an Indicators structure and return it as new. optimized
    //    indicators of the decision table.
    return Indicators.newBuilder()
        .countOfConditions(countOfConditions)
        .countOfActions(countOfActions)
        .orientation(Orientation.ROW)
        .content(consolidatedIndicators)
        .build();
  }

  private static Function<List<Indicator[]>, Stream<Indicator[]>> consolidateRuleGroup(
      int countOfConditions) {
    return ruleGroup -> {
      // buffer for current rule group during looping over their rule conditions
      final ArrayList<Indicator[]> currentGroup = Lists.newArrayList(ruleGroup);

      // loop over the rule conditions
      for (int loop = 0; loop < countOfConditions; loop++) {

        // use a MultiMap as buffer for grouping the rules. The sort criteria of the
        // TreeMap depends on the current loop value
        final ListMultimap<Indicator[], Indicator[]> multimap =
            Multimaps.newListMultimap(
                new TreeMap<>(compareConditions(countOfConditions, loop)), LinkedList::new);

        // -- each rule with a '-' at index 'loop' must be excluded from the further processing
        //    Because all rules, also the from the comparison excluded rules are necessary for the
        //    further processing, we split the currentGroup into two subsets. The one with the key
        //    Boolean.TRUE contains the excluded rules and the other, with the key Boolean.FALSE
        //    holds the rules which have to be compared.
        final int cursor = loop;
        final Map<Boolean, List<Indicator[]>> verifiedGroup =
            currentGroup.stream().collect(Collectors.partitioningBy(s -> s[cursor].sign() == MI));

        // put all group members into the multimap. They were grouped automatically
        verifiedGroup.get(Boolean.FALSE).forEach(e -> multimap.put(e, e));

        // cleanup the multimap by reducing their members to 1 if the size of an entry is greater 1
        for (Indicator[] k : multimap.keySet()) {
          final List<Indicator[]> v = multimap.get(k);
          int sz = v.size();

          if (sz > 1) {
            Indicator[] s = Arrays.copyOf(k, k.length);
            s[loop] = Indicator.of(MI, s[loop].row(), s[loop].col());
            ArrayList<Indicator[]> values = new ArrayList<>();
            values.add(s);
            multimap.replaceValues(k, values);
          }
        }
        // cleanup the current group buffer and ...
        currentGroup.clear();
        // ... initialize it with the new values from comparison ...
        currentGroup.addAll(multimap.values());
        // ... and the rules excluded from comparison.
        currentGroup.addAll(verifiedGroup.get(Boolean.TRUE));
      }
      return currentGroup.stream();
    };
  }

  static Comparator<Indicator[]> compareConditions(int condCount, int loopVar) {
    return (l, r) -> {
      int ret = l.length - r.length, q = 0;
      if (ret != 0) {
        throw new IllegalStateException("Error inconsistent rule definitions");
      }
      for (int k = 0; k < condCount; k++) {
        if (k == loopVar) {
          continue;
        }
        if ((q = l[k].sign() - r[k].sign()) != 0) {
          break;
        }
      }
      return q;
    };
  }

  public static BitSet indicatorsComplete(Indicators indicators) {
    // filter all indicators per condition
    final Indicator[][] crows =
        indicators
            .rows()
            .filter(r -> Arrays.stream(r).noneMatch(ind -> ind.sign() == XX))
            .toArray(Indicator[][]::new);

    // we're using a bit set as marker container for the rows which fulfill the requirement
    BitSet result = new BitSet(crows.length);

    // initialize the bitset
    for (int i = 0; i < crows.length; i++) {

      if (Arrays.stream(crows[i]).anyMatch(c -> c.sign() == YY)
          && Arrays.stream(crows[i]).anyMatch(c -> c.sign() == NN)) {
        result.set(i);
      } else {
        break;
      }
    }
    return result;
  }
}
