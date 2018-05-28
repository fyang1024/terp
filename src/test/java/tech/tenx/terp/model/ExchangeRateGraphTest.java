/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tenx.terp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateGraphTest {

    private static ExchangeCurrency BITFINEX_BTC;
    private static ExchangeCurrency BITFINEX_ETH;
    private static ExchangeCurrency BITFINEX_USD;

    private static ExchangeCurrency GDAX_BTC;
    private static ExchangeCurrency GDAX_USD;

    private static ExchangeCurrency KRAKEN_ETH;
    private static ExchangeCurrency KRAKEN_PAY;
    private static ExchangeCurrency KRAKEN_USD;
    
    private static ExchangeRate BITFINEX_BTC_ETH;
    private static ExchangeRate BITFINEX_ETH_BTC;
    private static ExchangeRate BITFINEX_BTC_USD;
    private static ExchangeRate BITFINEX_USD_BTC;
    private static ExchangeRate BITFINEX_ETH_USD;
    private static ExchangeRate BITFINEX_USD_ETH;

    private static ExchangeRate GDAX_BTC_USD;
    private static ExchangeRate GDAX_USD_BTC;

    private static ExchangeRate KRAKEN_ETH_USD;
    private static ExchangeRate KRAKEN_USD_ETH;
    private static ExchangeRate KRAKEN_PAY_ETH;
    private static ExchangeRate KRAKEN_ETH_PAY;

    @BeforeAll
    static void setup() {

        String BITFINEX = "bitfinex";
        String GDAX = "gdax";
        String KRAKEN = "kraken";

        String BTC = "btc";
        String ETH = "eth";
        String PAY = "pay";
        String USD = "usd";


        BITFINEX_BTC = new ExchangeCurrency(BITFINEX, BTC);
        BITFINEX_ETH = new ExchangeCurrency(BITFINEX, ETH);
        BITFINEX_USD = new ExchangeCurrency(BITFINEX, USD);

        GDAX_BTC = new ExchangeCurrency(GDAX, BTC);
        GDAX_USD = new ExchangeCurrency(GDAX, USD);

        KRAKEN_ETH = new ExchangeCurrency(KRAKEN, ETH);
        KRAKEN_PAY = new ExchangeCurrency(KRAKEN, PAY);
        KRAKEN_USD = new ExchangeCurrency(KRAKEN, USD);

        /* Changing rate values below can break bestRateCorrect tests */
        BITFINEX_BTC_ETH = new ExchangeRate(OffsetDateTime.now(), BITFINEX_BTC, BITFINEX_ETH, new BigDecimal("10"));
        BITFINEX_ETH_BTC = new ExchangeRate(OffsetDateTime.now(), BITFINEX_ETH, BITFINEX_BTC, new BigDecimal("0.099"));
        BITFINEX_BTC_USD = new ExchangeRate(OffsetDateTime.now(), BITFINEX_BTC, BITFINEX_USD, new BigDecimal("10000"));
        BITFINEX_USD_BTC = new ExchangeRate(OffsetDateTime.now(), BITFINEX_USD, BITFINEX_BTC, new BigDecimal("0.0000983"));
        BITFINEX_ETH_USD = new ExchangeRate(OffsetDateTime.now(), BITFINEX_ETH, BITFINEX_USD, new BigDecimal("1000"));
        BITFINEX_USD_ETH = new ExchangeRate(OffsetDateTime.now(), BITFINEX_USD, BITFINEX_ETH, new BigDecimal("0.000982"));

        GDAX_BTC_USD = new ExchangeRate(OffsetDateTime.now(), GDAX_BTC, GDAX_USD, new BigDecimal("9999.9"));
        GDAX_USD_BTC = new ExchangeRate(OffsetDateTime.now(), GDAX_USD, GDAX_BTC, new BigDecimal("0.0001"));

        KRAKEN_ETH_USD = new ExchangeRate(OffsetDateTime.now(), KRAKEN_ETH, KRAKEN_USD, new BigDecimal("1001"));
        KRAKEN_USD_ETH = new ExchangeRate(OffsetDateTime.now(), KRAKEN_USD, KRAKEN_ETH, new BigDecimal("0.000981"));
        KRAKEN_PAY_ETH = new ExchangeRate(OffsetDateTime.now(), KRAKEN_PAY, KRAKEN_ETH, new BigDecimal("0.002"));
        KRAKEN_ETH_PAY = new ExchangeRate(OffsetDateTime.now(), KRAKEN_ETH, KRAKEN_PAY, new BigDecimal("499"));
    }

    private ExchangeRateGraph exchangeRateGraph = null;

    @BeforeEach
    void init() {
        exchangeRateGraph = new ExchangeRateGraph();
    }

    @Test
    void exchangeRateAdded() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_ETH);
        assertEquals(BITFINEX_BTC_ETH, exchangeRateGraph.getExchangeRate(BITFINEX_BTC, BITFINEX_ETH));
    }

    @Test
    void exchangeRateUpdated() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_ETH);
        OffsetDateTime newTime = BITFINEX_BTC_ETH.getTimestamp().plusSeconds(1);
        ExchangeRate newRate = new ExchangeRate(newTime, BITFINEX_BTC, BITFINEX_ETH, new BigDecimal("10.1"));
        exchangeRateGraph.addOrUpdateExchangeRate(newRate);
        assertEquals(newRate, exchangeRateGraph.getExchangeRate(BITFINEX_BTC, BITFINEX_ETH));
    }

    @Test
    void exchangeRateNotUpdated() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_ETH);
        OffsetDateTime oldTime = BITFINEX_BTC_ETH.getTimestamp().minusSeconds(1);
        ExchangeRate oldRate = new ExchangeRate(oldTime, BITFINEX_BTC, BITFINEX_ETH, new BigDecimal("10.1"));
        exchangeRateGraph.addOrUpdateExchangeRate(oldRate);
        assertNotEquals(oldRate, exchangeRateGraph.getExchangeRate(BITFINEX_BTC, BITFINEX_ETH));
    }

    @Test
    void sameCurrencyRatesAdded() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(GDAX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_ETH_USD);
        assertNotNull(exchangeRateGraph.getExchangeRate(BITFINEX_BTC, GDAX_BTC));
        assertNotNull(exchangeRateGraph.getExchangeRate(GDAX_BTC, BITFINEX_BTC));
        assertNotNull(exchangeRateGraph.getExchangeRate(BITFINEX_USD, GDAX_USD));
        assertNotNull(exchangeRateGraph.getExchangeRate(GDAX_USD, BITFINEX_USD));
        assertNotNull(exchangeRateGraph.getExchangeRate(BITFINEX_USD, KRAKEN_USD));
        assertNotNull(exchangeRateGraph.getExchangeRate(KRAKEN_USD, BITFINEX_USD));
        assertNotNull(exchangeRateGraph.getExchangeRate(GDAX_USD, KRAKEN_USD));
        assertNotNull(exchangeRateGraph.getExchangeRate(KRAKEN_USD, GDAX_USD));
    }

    @Test
    void sameCurrencyRateBeOne() {
        exchangeRateGraph.addOrUpdateExchangeRate(GDAX_USD_BTC);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_USD_ETH);
        assertEquals(exchangeRateGraph.getExchangeRate(GDAX_USD, KRAKEN_USD).getRate(), BigDecimal.ONE);
        assertEquals(exchangeRateGraph.getExchangeRate(KRAKEN_USD, GDAX_USD).getRate(), BigDecimal.ONE);
    }

    @Test
    void diffCurrencyRatesNotAdded() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(GDAX_BTC_USD);
        assertNull(exchangeRateGraph.getExchangeRate(BITFINEX_BTC, GDAX_USD));
        assertNull(exchangeRateGraph.getExchangeRate(BITFINEX_USD, GDAX_BTC));
        assertNull(exchangeRateGraph.getExchangeRate(GDAX_BTC, BITFINEX_USD));
        assertNull(exchangeRateGraph.getExchangeRate(GDAX_USD, BITFINEX_BTC));
    }

    @Test
    void bestRateCorrect1() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_USD_BTC);


        BestRateResponse response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, BITFINEX_BTC));
        assertEquals(response.getRate(), BigDecimal.ONE);
        assertTrue(response.getPath().isEmpty());
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_USD, BITFINEX_USD));
        assertEquals(response.getRate(), BigDecimal.ONE);
        assertTrue(response.getPath().isEmpty());
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, BITFINEX_USD));
        assertEquals(response.getRate(), BITFINEX_BTC_USD.getRate());
        assertEquals(response.getPath().getSteps().size(), 2);
        assertFalse(response.getPath().isCircular());


        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_USD, BITFINEX_BTC));
        assertEquals(response.getRate(), BITFINEX_USD_BTC.getRate());
        assertEquals(response.getPath().getSteps().size(), 2);
        assertFalse(response.getPath().isCircular());
    }

    @Test
    void bestRateCorrect2() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_USD_BTC);
        exchangeRateGraph.addOrUpdateExchangeRate(GDAX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(GDAX_USD_BTC);

        BestRateResponse response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, BITFINEX_USD));
        assertEquals(response.getRate(), BITFINEX_BTC_USD.getRate());
        assertEquals(response.getPath().getSteps().size(), 2);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, GDAX_USD));
        assertEquals(response.getRate(), BITFINEX_BTC_USD.getRate());
        assertEquals(response.getPath().getSteps().size(), 3);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(GDAX_BTC, BITFINEX_USD));
        assertEquals(response.getRate(), BITFINEX_BTC_USD.getRate());
        assertEquals(response.getPath().getSteps().size(), 3);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(GDAX_BTC, GDAX_USD));
        assertEquals(response.getRate(), BITFINEX_BTC_USD.getRate());
        assertEquals(response.getPath().getSteps().size(), 4);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(GDAX_USD, GDAX_BTC));
        assertEquals(response.getRate(), GDAX_USD_BTC.getRate());
        assertEquals(response.getPath().getSteps().size(), 2);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(GDAX_USD, BITFINEX_BTC));
        assertEquals(response.getRate(), GDAX_USD_BTC.getRate());
        assertEquals(response.getPath().getSteps().size(), 3);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_USD, GDAX_BTC));
        assertEquals(response.getRate(), GDAX_USD_BTC.getRate());
        assertEquals(response.getPath().getSteps().size(), 3);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_USD, BITFINEX_BTC));
        assertEquals(response.getRate(), GDAX_USD_BTC.getRate());
        assertEquals(response.getPath().getSteps().size(), 4);
        assertFalse(response.getPath().isCircular());
    }

    @Test
    void bestRateCorrect3() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_USD_BTC);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_PAY_ETH);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_ETH_PAY);

        BestRateResponse response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, KRAKEN_PAY));
        assertNull(response.getRate());
        assertTrue(response.getPath().isEmpty());
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(KRAKEN_ETH, BITFINEX_USD));
        assertNull(response.getRate());
        assertTrue(response.getPath().isEmpty());
        assertFalse(response.getPath().isCircular());
    }

    @Test
    void bestRateCorrect4() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_USD_BTC);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_ETH);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_ETH_BTC);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_ETH_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_USD_ETH);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_ETH_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_USD_ETH);

        BestRateResponse response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, KRAKEN_ETH));
        assertEquals(response.getRate(), BITFINEX_BTC_ETH.getRate());
        assertEquals(response.getPath().getSteps().size(), 3);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(KRAKEN_USD, BITFINEX_ETH));
        assertEquals(response.getRate(), BITFINEX_USD_BTC.getRate().multiply(BITFINEX_BTC_ETH.getRate()));
        assertEquals(response.getPath().getSteps().size(), 4);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_USD, BITFINEX_ETH));
        assertEquals(response.getRate(), BITFINEX_USD_BTC.getRate().multiply(BITFINEX_BTC_ETH.getRate()));
        assertEquals(response.getPath().getSteps().size(), 3);
        assertFalse(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_ETH, BITFINEX_USD));
        assertEquals(response.getRate(), KRAKEN_ETH_USD.getRate());
        assertEquals(response.getPath().getSteps().size(), 4);
        assertFalse(response.getPath().isCircular());
    }

    @Test
    void bestRateCorrect5() {
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_USD_BTC);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_BTC_ETH);
        exchangeRateGraph.addOrUpdateExchangeRate(BITFINEX_ETH_BTC);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_ETH_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(KRAKEN_USD_ETH);
        exchangeRateGraph.addOrUpdateExchangeRate(GDAX_BTC_USD);
        exchangeRateGraph.addOrUpdateExchangeRate(GDAX_USD_BTC);

        BestRateResponse response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, KRAKEN_ETH));
        assertEquals(response.getRate(), Double.POSITIVE_INFINITY);
        assertTrue(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, KRAKEN_ETH));
        assertEquals(response.getRate(), Double.POSITIVE_INFINITY);
        assertTrue(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_BTC, GDAX_BTC));
        assertEquals(response.getRate(), Double.POSITIVE_INFINITY);
        assertTrue(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(GDAX_USD, KRAKEN_USD));
        assertEquals(response.getRate(), Double.POSITIVE_INFINITY);
        assertTrue(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(BITFINEX_ETH, BITFINEX_ETH));
        assertEquals(response.getRate(), Double.POSITIVE_INFINITY);
        assertTrue(response.getPath().isCircular());

        response = exchangeRateGraph.getBestRate(new BestRateRequest(GDAX_USD, KRAKEN_PAY));
        assertNull(response.getRate());
        assertTrue(response.getPath().isEmpty());
        assertFalse(response.getPath().isCircular());
    }
}
