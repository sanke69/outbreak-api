/**
 * JavaFR
 * Copyright (C) 2007-?XYZ  Steve PECHBERTI <steve.pechberti@laposte.net>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.geodesic.referential.api.countries;

import java.util.Comparator;
import java.util.Locale;

/**
 * The list of countries still OK in 2020 during the Covid-19 Pandemy
 * 
 * @author sanke
 *
 */
public enum Country {
	UNKNOWN, WORLD, GROUP, EDEN, ATLANTIS, WAKANDA,
    AD, AE, AF, AG, AI, AL, AM, AO, AR, AS, AT, AU, AW, AX, AZ,
    BA, BB, BD, BE, BF, BG, BH, BI, BJ, BL, BN, BO, BM, BQ, BR, BS, BT, BV, BW, BY, BZ,
    CA, CC, CD, CF, CG, CH, CI, CK, CL, CM, CN, CO, CR, CU, CV, CW, CX, CY, CZ,
    DE, DJ, DK, DM, DO, DZ,
    EC, EG, EE, EH, ER, ES, ET,
    FI, FJ, FK, FM, FO, FR,
    GA, GB, GE, GD, GF, GG, GH, GI, GL, GM, GN, GO, GP, GQ, GR, GS, GT, GU, GW, GY,
    HK, HM, HN, HR, HT, HU,
    ID, IE, IL, IM, IN, IO, IQ, IR, IS, IT,
    JE, JM, JO, JP, JU,
    KE, KG, KH, KI, KM, KN, KP, KR, XK, KV, KW, KY, KZ,
    LA, LB, LC, LI, LK, LR, LS, LT, LU, LV, LY,
    MA, MC, MD, MG, ME, MF, MH, MK, ML, MO, MM, MN, MP, MQ, MR, MS, MT, MU, MV, MW, MX, MY, MZ,
    NA, NC, NE, NF, NG, NI, NL, NO, NP, NR, NU, NZ,
    OM,
    PA, PE, PF, PG, PH, PK, PL, PM, PN, PR, PS, PT, PW, PY,
    QA,
    RE, RO, RS, RU, RW,
    SA, SB, SC, SD, SE, SG, SH, SI, SJ, SK, SL, SM, SN, SO, SR, SS, ST, SV, SX, SY, SZ,
    TC, TD, TF, TG, TH, TJ, TK, TL, TM, TN, TO, TR, TT, TV, TW, TZ,
    UA, UG, UM_DQ, UM_FQ, UM_HQ, UM_JQ, UM_MQ, UM_WQ, US, UY, UZ,
    VA, VC, VE, VG, VI, VN, VU,
    WF, WS,
    YE, YT,
    ZA, ZM, ZW;

	public static final Comparator<Country> nameComparator = (c1, c2) -> c1.getName().compareTo( c2.getName() );

    public static Country of(String _value) {
    	try {
    		return Country.valueOf(_value);
    	} catch(Exception e) {
    		return Country.UNKNOWN;
    	}
    }

    Country() {
        ;
    }

    public String getName() { return new Locale("", name()).getDisplayCountry(); }

    public String iso2() { return name(); }
    public String iso3() {
    	return new Locale("", name()).getISO3Country();
    }

}
