/**
 * Copyright (c) 2021 SIL International
 * This software is licensed under the LGPL, version 2.1 or later
 * (http://www.gnu.org/licenses/lgpl-2.1.html)
 */
package org.sil.syllableparser.service.parsing;

import org.junit.Before;
import org.junit.Test;
import org.sil.syllableparser.Constants;

/**
 * @author Andy Black
 *
 */
public class NPSyllabifierWithFilterTest extends NPSyllabifierTestBase {

	@Before
	public void setUp() throws Exception {
		this.projectFile = Constants.UNIT_TEST_DATA_FILE_TEMPLATES_FILTERS;
		super.setUp();
		}

	@Test
	public void npFilterWithConstituentBeginMarkerTest() {
		checkSyllabification("ʃɹəb", true, 1, "ʃɹəb", "(W(σ(N''(\\L ʃ(\\G ʃ))(\\L ɹ(\\G ɹ))(N'(N(\\L ə(\\G ə)))(\\L b(\\G b))))))");
		checkSyllabification("kæʃlɛs", true, 2, "kæʃ.lɛs", "(W(σ(N''(\\L k(\\G k))(N'(N(\\L æ(\\G æ)))(\\L ʃ(\\G ʃ)))))(σ(N''(\\L l(\\G l))(N'(N(\\L ɛ(\\G ɛ)))(\\L s(\\G s))))))");
		checkSyllabification("maɹʃlænd", true, 2, "maɹʃ.lænd", "(W(σ(N''(\\L m(\\G m))(N'(N(\\L a(\\G a)))(\\L ɹ(\\G ɹ))(\\L ʃ(\\G ʃ)))))(σ(N''(\\L l(\\G l))(N'(N(\\L æ(\\G æ)))(\\L n(\\G n))(\\L d(\\G d))))))");
	}
}
