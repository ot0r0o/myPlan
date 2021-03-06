package com.conzebit.myplan.ext.es.movistar.particulares;

import java.util.ArrayList;

import com.conzebit.myplan.core.Chargeable;
import com.conzebit.myplan.core.call.Call;

import com.conzebit.myplan.core.message.ChargeableMessage;
import com.conzebit.myplan.core.msisdn.MsisdnType;
import com.conzebit.myplan.core.plan.PlanChargeable;
import com.conzebit.myplan.core.plan.PlanSummary;
import com.conzebit.myplan.core.sms.Sms;
import com.conzebit.myplan.ext.es.movistar.ESMovistar;


/**
 * Movistar Contrato móviles Movistar.
 * http://www.tarifas.movistar.es/web/movistar/detalle-tarifas-voz-particulares?tipo=tarifas&name=Contrato%20M%C3%B3viles%20Movistar&impuesto=Sin%20impuestos&idContenido=c7003a01-4cdb-4c13-9213-365c4fb7d0ee
 * @author sanz
 */
public class ESMovistarContratoMoviles extends ESMovistar {
    
	private double minimumMonthFee = 9;
	private double initialPrice = 0.15;
	private double pricePerSecond = 0.30 / 60;
	private double pricePerSecondMovistar = 0.03 / 60;
	private double smsPrice = 0.15;
	//private double mmsPrice = 1.0;
    
	public String getPlanName() {
		return "Contrato Móviles Movistar";
	}
	
	public String getPlanURL() {
		return "http://www.tarifas.movistar.es/web/movistar/detalle-tarifas-voz-particulares?tipo=tarifas&name=Contrato%20M%C3%B3viles%20Movistar&impuesto=Sin%20impuestos&idContenido=c7003a01-4cdb-4c13-9213-365c4fb7d0ee";
	}
	
	public PlanSummary process(ArrayList<Chargeable> data) {
		PlanSummary ret = new PlanSummary(this);
		double globalPrice = 0;
		for (Chargeable chargeable : data) {
			if (chargeable.getChargeableType() == Chargeable.CHARGEABLE_TYPE_CALL) {
				Call call = (Call) chargeable;

				if (call.getType() != Call.CALL_TYPE_SENT) {
					continue;
				}
	
				double callPrice = 0;
	
				if (call.getContact().getMsisdnType() == MsisdnType.ES_SPECIAL_ZER0) {
					callPrice = 0;
				} else if (call.getContact().getMsisdnType() == MsisdnType.ES_MOVISTAR) {
					callPrice = initialPrice + (call.getDuration() * pricePerSecondMovistar);
				} else {
					callPrice = initialPrice + (call.getDuration() * pricePerSecond);
				}
				globalPrice += callPrice;
				ret.addPlanCall(new PlanChargeable(call, callPrice, this.getCurrency()));
			} else if (chargeable.getChargeableType() == Chargeable.CHARGEABLE_TYPE_SMS) {
				Sms sms = (Sms) chargeable;
				if (sms.getType() == Sms.SMS_TYPE_RECEIVED) {
					continue;
				}
				globalPrice += smsPrice;
				ret.addPlanCall(new PlanChargeable(chargeable, smsPrice, this.getCurrency()));
			}
		}
		if (globalPrice < minimumMonthFee) {
			ret.addPlanCall(new PlanChargeable(new ChargeableMessage(ChargeableMessage.MESSAGE_MINIMUM_MONTH_FEE), minimumMonthFee - globalPrice, this.getCurrency()));
		}
		return ret;
	}
}