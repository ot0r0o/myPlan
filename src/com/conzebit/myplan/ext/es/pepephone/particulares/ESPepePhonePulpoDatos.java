/*
 * This file is part of myPlan.
 *
 * Plan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Plan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with myPlan.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.conzebit.myplan.ext.es.pepephone.particulares;

import java.util.Map;

import com.conzebit.myplan.core.call.Call;
import com.conzebit.myplan.core.msisdn.MsisdnType;
import com.conzebit.myplan.core.plan.PlanChargeable.Type;
import com.conzebit.myplan.core.sms.Sms;
import com.conzebit.myplan.ext.es.pepephone.ESPepePhone;


/**
 * PepePhone Pulpo + Datos
 * 
 * @author sanz
 */
public class ESPepePhonePulpoDatos extends ESPepePhone {
    
	private double initialPrice = 0.15;
	private double smsPrice = 0.09;
	private double monthFee = 6.9;
    
	public String getPlanName() {
		return "Pulpo Pepe + Datos";
	}
	
	public String getPlanURL() {
		return "http://www.pepephone.com/promo/adslzone-pulpopepe/index.html";
	}
	
	public Double getMonthFee() {
		return monthFee;
	}
	
	public ProcessResult processCall(Call call, Map<String, Object> accumulatedData) {
		if (call.getType() != Call.CALL_TYPE_SENT) {
			return null;
		}

		ProcessResult ret = new ProcessResult();
		if (call.getContact().getMsisdnType() == MsisdnType.ES_SPECIAL_ZER0) {
			ret.price = 0.0;
			ret.type = Type.ZERO;
		} else {
			double pricePerSecond = (int) (call.getDuration() / 60);
			pricePerSecond = ((pricePerSecond >= 4) ? 0.040 : pricePerSecond / 100) / 60;
			ret.price = initialPrice + (call.getDuration() * pricePerSecond);
			ret.type = Type.INSIDE_PLAN;
		}
		return ret;
	}

	public ProcessResult processSms(Sms sms, Map<String, Object> accumulatedData) {
		if (sms.getType() != Sms.SMS_TYPE_SENT) {
			return null;
		}
		ProcessResult ret = new ProcessResult();
		ret.price = smsPrice;
		ret.type = Type.INSIDE_PLAN;
		return ret;
	}
}