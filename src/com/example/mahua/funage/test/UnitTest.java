package com.example.mahua.funage.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.test.AndroidTestCase;
import android.text.TextUtils;

import com.example.mahua.funage.domain.Ticket;
import com.example.mahua.funage.utils.Utils;

public class UnitTest extends AndroidTestCase {
	
	private void sortTickets(List<Ticket> tickets) {
		Collections.sort(tickets, new Comparator<Ticket>() {
			
			@Override
			public int compare(Ticket lhs, Ticket rhs) {
				String isPrint_lhs_String = lhs.getIsPrint();
				String isPrint_rhs_String = rhs.getIsPrint();
				if (TextUtils.isEmpty(isPrint_lhs_String) || !Utils.isNum(isPrint_lhs_String)) {
					isPrint_lhs_String = "0";
				}
				if (TextUtils.isEmpty(isPrint_rhs_String) || !Utils.isNum(isPrint_rhs_String)) {
					isPrint_rhs_String = "0";
				}
				int isPrint_lhs = Integer.parseInt(isPrint_lhs_String);
				int isPrint_rhs = Integer.parseInt(isPrint_rhs_String);
				if (isPrint_lhs > isPrint_rhs) {
					return 1;
					
				} else if (isPrint_lhs < isPrint_rhs) {
					return -1;
				}
				return 0;
			}
			
		});
		
	}
	
	public void pendingTestone() {
		List<Ticket> tickets = new ArrayList<Ticket>();
		Ticket t1 = new Ticket();
		t1.setIsPrint("0");
		Ticket t2 = new Ticket();
		t2.setIsPrint("1");
		
		tickets.add(t1);
		tickets.add(t2);
		sortTickets(tickets);
		System.out.println(tickets.toString());
	}
	
	public void testFileRN() {
		
		String name = "http://qaza2008.free.cscces.net/app/mahua1002.apk";
		File newPath = new File(name.substring(0, name.lastIndexOf(".")) + "");
	}
}
