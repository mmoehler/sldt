package com.mmoehler.sldt;

/*-
 * #%L
 * sldt
 * %%
 * Copyright (C) 2021 Michael Moehler
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LimitedEntryDecisionTableTest {

  static LimitedEntryDecisionTable<Person, String> dt;

  enum Sex {
    MALE,
    FEMALE
  }

  static class Person {
    final String name;
    final int age;
    final Sex sex;

    public Person(String name, int age, Sex sex) {
      this.name = name;
      this.age = age;
      this.sex = sex;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    public Sex getSex() {
      return sex;
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  @BeforeAll
  public static void setupClass() {
    dt =
        LimitedEntryDecisionTable.<Person, String>newBuilder()
            .conditions(
                (p) -> p.getName() != null,
                (p) -> p.getSex() == Sex.MALE,
                (p) -> p.getAge() > 0 && p.age <= 21)
            .actions(
                (p) -> p.getName() + " is a young boy",
                (p) -> p.getName() + " is an adult man",
                (p) -> p.getName() + " is a young girl",
                (p) -> p.getName() + " is an adult woman")
            .elseAction(
                (p) -> {
                  throw new IllegalStateException("Unnamed person!");
                })
            .indicators(""
                    /* @formatter::off */
                    + "YYYY"
                    + "YYNN"
                    + "YNYN"
                    + "X---"
                    + "-X--"
                    + "--X-"
                    + "---X")
                /* @formatter::on */
            .build();
  }

  @Test
  void applyR1() {
    final String name = "Charly";
    final String expected = String.format("%s is a young girl", name);
    final String actual = dt.apply(new Person(name, 15, Sex.FEMALE));
    assertEquals(expected, actual);
  }

  @Test
  void applyR2() {
    final String name = "Charly";
    final String expected = String.format("%s is an adult woman", name);
    final String actual = dt.apply(new Person(name, 50, Sex.FEMALE));
    assertEquals(expected, actual);
  }

  @Test
  void applyR3() {
    final String name = "Charly";
    final String expected = String.format("%s is a young boy", name);
    final String actual = dt.apply(new Person(name, 15, Sex.MALE));
    assertEquals(expected, actual);
  }

  @Test
  void applyR4() {
    final String name = "Charly";
    final String expected = String.format("%s is an adult man", name);
    final String actual = dt.apply(new Person(name, 40, Sex.MALE));
    assertEquals(expected, actual);
  }

  @Test
  void applyElse() {
    final Person person = new Person(null, 40, Sex.MALE);
    Throwable exception = assertThrows(IllegalStateException.class, () -> dt.apply(person));
    assertEquals("Unnamed person!", exception.getMessage());
  }
}
