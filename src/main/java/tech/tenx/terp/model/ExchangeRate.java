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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public class ExchangeRate {

    private OffsetDateTime timestamp;

    private ExchangeCurrency srcCurrency;

    private ExchangeCurrency destCurrency;

    private BigDecimal rate;

    public ExchangeRate(OffsetDateTime timestamp, ExchangeCurrency srcCurrency, ExchangeCurrency destCurrency, BigDecimal rate) {
        this.timestamp = timestamp;
        this.srcCurrency = srcCurrency;
        this.destCurrency = destCurrency;
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(srcCurrency, that.srcCurrency) &&
                Objects.equals(destCurrency, that.destCurrency) &&
                Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timestamp, srcCurrency, destCurrency, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "timestamp=" + timestamp +
                ", srcCurrency=" + srcCurrency +
                ", destCurrency=" + destCurrency +
                ", rate=" + rate +
                '}';
    }

    public BigDecimal getRate() {
        return rate;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public ExchangeCurrency getSrcCurrency() {
        return srcCurrency;
    }

    public ExchangeCurrency getDestCurrency() {
        return destCurrency;
    }

    boolean isOlderThan(ExchangeRate other) {
        return timestamp.isBefore(other.timestamp);
    }
}
