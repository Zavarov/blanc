/*
 * Copyright (c) 2019 Zavarov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package vartas.discord.bot.command.parameter._symboltable;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

public class DateSymbol extends DateSymbolTOP{
    protected SimpleDateFormat dateFormat;
    protected BigDecimal day;
    protected BigDecimal month;
    protected BigDecimal year;

    public DateSymbol(String name) {
        super(name);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void setValue(BigDecimal day, BigDecimal month, BigDecimal year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Optional<Date> resolve(){
        try{
            Date date = dateFormat.parse(String.format("%2d-%2d-%4d", day.intValueExact(), month.intValueExact(), year.intValueExact()));
            return Optional.of(date);
        }catch(ParseException e){
            return Optional.empty();
        }
    }
}
