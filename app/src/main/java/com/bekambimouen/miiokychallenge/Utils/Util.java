package com.bekambimouen.miiokychallenge.Utils;

import android.view.View;
import android.widget.ImageView;

public class Util {
    // Method fo prevent double click
    public static void preventTwoClick(final View view){
        view.setEnabled(false);
        view.postDelayed(
                ()-> view.setEnabled(true),
                3000
        );
    }

    // countries flag
    public static void getCountryFlag(String countryID, ImageView flag) {
        if (countryID.equals("af"))
            flag.setImageResource(com.blongho.country_data.R.drawable.af);
        if (countryID.equals("al"))
            flag.setImageResource(com.blongho.country_data.R.drawable.al);
        if (countryID.equals("dz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.dz);
        if (countryID.equals("ad"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ad);
        if (countryID.equals("ao"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ao);
        if (countryID.equals("aq"))
            flag.setImageResource(com.blongho.country_data.R.drawable.aq);
        if (countryID.equals("ar"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ar);
        if (countryID.equals("am"))
            flag.setImageResource(com.blongho.country_data.R.drawable.am);
        if (countryID.equals("aw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.aw);
        if (countryID.equals("au"))
            flag.setImageResource(com.blongho.country_data.R.drawable.au);
        if (countryID.equals("at"))
            flag.setImageResource(com.blongho.country_data.R.drawable.at);
        if (countryID.equals("az"))
            flag.setImageResource(com.blongho.country_data.R.drawable.az);
        if (countryID.equals("bh"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bh);
        if (countryID.equals("bd"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bd);
        if (countryID.equals("by"))
            flag.setImageResource(com.blongho.country_data.R.drawable.by);
        if (countryID.equals("be"))
            flag.setImageResource(com.blongho.country_data.R.drawable.be);
        if (countryID.equals("bz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bz);
        if (countryID.equals("bj"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bj);
        if (countryID.equals("bt"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bt);
        if (countryID.equals("bo"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bo);
        if (countryID.equals("ba"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ba);
        if (countryID.equals("bw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bw);
        if (countryID.equals("br"))
            flag.setImageResource(com.blongho.country_data.R.drawable.br);
        if (countryID.equals("bn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bn);
        if (countryID.equals("bg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bg);
        if (countryID.equals("bf"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bf);
        if (countryID.equals("mm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mm);
        if (countryID.equals("bi"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bi);
        if (countryID.equals("kh"))
            flag.setImageResource(com.blongho.country_data.R.drawable.kh);
        if (countryID.equals("cm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cm);
        if (countryID.equals("ca"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ca);
        if (countryID.equals("cv"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cv);
        if (countryID.equals("cf"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cf);
        if (countryID.equals("td"))
            flag.setImageResource(com.blongho.country_data.R.drawable.td);
        if (countryID.equals("cl"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cl);
        if (countryID.equals("cn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cn);
        if (countryID.equals("cx"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cx);
        if (countryID.equals("cc"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cc);
        if (countryID.equals("co"))
            flag.setImageResource(com.blongho.country_data.R.drawable.co);
        if (countryID.equals("km"))
            flag.setImageResource(com.blongho.country_data.R.drawable.km);
        if (countryID.equals("cg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cg);
        if (countryID.equals("cd"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cd);
        if (countryID.equals("ck"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ck);
        if (countryID.equals("cr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cr);
        if (countryID.equals("hr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.hr);
        if (countryID.equals("cu"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cu);
        if (countryID.equals("cy"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cy);
        if (countryID.equals("cz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.cz);
        if (countryID.equals("dk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.dk);
        if (countryID.equals("dj"))
            flag.setImageResource(com.blongho.country_data.R.drawable.dj);
        if (countryID.equals("tl"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tl);
        if (countryID.equals("ec"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ec);
        if (countryID.equals("eg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.eg);
        if (countryID.equals("sv"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sv);
        if (countryID.equals("gq"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gq);
        if (countryID.equals("er"))
            flag.setImageResource(com.blongho.country_data.R.drawable.er);
        if (countryID.equals("ee"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ee);
        if (countryID.equals("et"))
            flag.setImageResource(com.blongho.country_data.R.drawable.et);
        if (countryID.equals("fk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.fk);
        if (countryID.equals("fo"))
            flag.setImageResource(com.blongho.country_data.R.drawable.fo);
        if (countryID.equals("fj"))
            flag.setImageResource(com.blongho.country_data.R.drawable.fj);
        if (countryID.equals("fi"))
            flag.setImageResource(com.blongho.country_data.R.drawable.fi);
        if (countryID.equals("fr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.fr);
        if (countryID.equals("pf"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pf);
        if (countryID.equals("ga"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ga);
        if (countryID.equals("gm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gm);
        if (countryID.equals("ge"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ge);
        if (countryID.equals("de"))
            flag.setImageResource(com.blongho.country_data.R.drawable.de);
        if (countryID.equals("gh"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gh);
        if (countryID.equals("gi"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gi);
        if (countryID.equals("gr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gr);
        if (countryID.equals("gl"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gl);
        if (countryID.equals("gt"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gt);
        if (countryID.equals("gn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gn);
        if (countryID.equals("gw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gw);
        if (countryID.equals("gy"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gy);
        if (countryID.equals("ht"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ht);
        if (countryID.equals("hn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.hn);
        if (countryID.equals("hk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.hk);
        if (countryID.equals("hu"))
            flag.setImageResource(com.blongho.country_data.R.drawable.hu);
        if (countryID.equals("in"))
            flag.setImageResource(com.blongho.country_data.R.drawable.in);
        if (countryID.equals("id"))
            flag.setImageResource(com.blongho.country_data.R.drawable.id);
        if (countryID.equals("ir"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ir);
        if (countryID.equals("iq"))
            flag.setImageResource(com.blongho.country_data.R.drawable.iq);
        if (countryID.equals("ie"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ie);
        if (countryID.equals("im"))
            flag.setImageResource(com.blongho.country_data.R.drawable.im);
        if (countryID.equals("il"))
            flag.setImageResource(com.blongho.country_data.R.drawable.il);
        if (countryID.equals("it"))
            flag.setImageResource(com.blongho.country_data.R.drawable.it);
        if (countryID.equals("ci"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ci);
        if (countryID.equals("jp"))
            flag.setImageResource(com.blongho.country_data.R.drawable.jp);
        if (countryID.equals("jo"))
            flag.setImageResource(com.blongho.country_data.R.drawable.jo);
        if (countryID.equals("kz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.kz);
        if (countryID.equals("ke"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ke);
        if (countryID.equals("ki"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ki);
        if (countryID.equals("kw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.kw);
        if (countryID.equals("kg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.kg);
        if (countryID.equals("la"))
            flag.setImageResource(com.blongho.country_data.R.drawable.la);
        if (countryID.equals("lv"))
            flag.setImageResource(com.blongho.country_data.R.drawable.lv);
        if (countryID.equals("lb"))
            flag.setImageResource(com.blongho.country_data.R.drawable.lb);
        if (countryID.equals("ls"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ls);
        if (countryID.equals("lr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.lr);
        if (countryID.equals("ly"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ly);
        if (countryID.equals("li"))
            flag.setImageResource(com.blongho.country_data.R.drawable.li);
        if (countryID.equals("lt"))
            flag.setImageResource(com.blongho.country_data.R.drawable.lt);
        if (countryID.equals("lu"))
            flag.setImageResource(com.blongho.country_data.R.drawable.lu);
        if (countryID.equals("mo"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mo);
        if (countryID.equals("mk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mk);
        if (countryID.equals("mg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mg);
        if (countryID.equals("mw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mw);
        if (countryID.equals("my"))
            flag.setImageResource(com.blongho.country_data.R.drawable.my);
        if (countryID.equals("mv"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mv);
        if (countryID.equals("ml"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ml);
        if (countryID.equals("mt"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mt);
        if (countryID.equals("mh"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mh);
        if (countryID.equals("mr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mr);
        if (countryID.equals("mu"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mu);
        if (countryID.equals("yt"))
            flag.setImageResource(com.blongho.country_data.R.drawable.yt);
        if (countryID.equals("mx"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mx);
        if (countryID.equals("fm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.fm);
        if (countryID.equals("md"))
            flag.setImageResource(com.blongho.country_data.R.drawable.md);
        if (countryID.equals("mc"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mc);
        if (countryID.equals("mn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mn);
        if (countryID.equals("me"))
            flag.setImageResource(com.blongho.country_data.R.drawable.me);
        if (countryID.equals("ma"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ma);
        if (countryID.equals("mz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.mz);
        if (countryID.equals("na"))
            flag.setImageResource(com.blongho.country_data.R.drawable.na);
        if (countryID.equals("nr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.nr);
        if (countryID.equals("np"))
            flag.setImageResource(com.blongho.country_data.R.drawable.np);
        if (countryID.equals("nl"))
            flag.setImageResource(com.blongho.country_data.R.drawable.nl);
        if (countryID.equals("an"))
            flag.setImageResource(com.blongho.country_data.R.drawable.an);
        if (countryID.equals("nc"))
            flag.setImageResource(com.blongho.country_data.R.drawable.nc);
        if (countryID.equals("nz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.nz);
        if (countryID.equals("ni"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ni);
        if (countryID.equals("ne"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ne);
        if (countryID.equals("ng"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ng);
        if (countryID.equals("nu"))
            flag.setImageResource(com.blongho.country_data.R.drawable.nu);
        if (countryID.equals("kp"))
            flag.setImageResource(com.blongho.country_data.R.drawable.kp);
        if (countryID.equals("no"))
            flag.setImageResource(com.blongho.country_data.R.drawable.no);
        if (countryID.equals("om"))
            flag.setImageResource(com.blongho.country_data.R.drawable.om);
        if (countryID.equals("pk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pk);
        if (countryID.equals("pw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pw);
        if (countryID.equals("pa"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pa);
        if (countryID.equals("pg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pg);
        if (countryID.equals("py"))
            flag.setImageResource(com.blongho.country_data.R.drawable.py);
        if (countryID.equals("pe"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pe);
        if (countryID.equals("ph"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ph);
        if (countryID.equals("pn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pn);
        if (countryID.equals("pl"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pl);
        if (countryID.equals("pt"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pt);
        if (countryID.equals("pr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pr);
        if (countryID.equals("qa"))
            flag.setImageResource(com.blongho.country_data.R.drawable.qa);
        if (countryID.equals("ro"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ro);
        if (countryID.equals("ru"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ru);
        if (countryID.equals("rw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.rw);
        if (countryID.equals("bl"))
            flag.setImageResource(com.blongho.country_data.R.drawable.bl);
        if (countryID.equals("ws"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ws);
        if (countryID.equals("sm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sm);
        if (countryID.equals("st"))
            flag.setImageResource(com.blongho.country_data.R.drawable.st);
        if (countryID.equals("sa"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sa);
        if (countryID.equals("sn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sn);
        if (countryID.equals("rs"))
            flag.setImageResource(com.blongho.country_data.R.drawable.rs);
        if (countryID.equals("sc"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sc);
        if (countryID.equals("sl"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sl);
        if (countryID.equals("sg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sg);
        if (countryID.equals("sk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sk);
        if (countryID.equals("si"))
            flag.setImageResource(com.blongho.country_data.R.drawable.si);
        if (countryID.equals("sb"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sb);
        if (countryID.equals("so"))
            flag.setImageResource(com.blongho.country_data.R.drawable.so);
        if (countryID.equals("za"))
            flag.setImageResource(com.blongho.country_data.R.drawable.za);
        if (countryID.equals("kr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.kr);
        if (countryID.equals("es"))
            flag.setImageResource(com.blongho.country_data.R.drawable.es);
        if (countryID.equals("lk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.lk);
        if (countryID.equals("sh"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sh);
        if (countryID.equals("pm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.pm);
        if (countryID.equals("sd"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sd);
        if (countryID.equals("sr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sr);
        if (countryID.equals("sz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sz);
        if (countryID.equals("se"))
            flag.setImageResource(com.blongho.country_data.R.drawable.se);
        if (countryID.equals("ch"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ch);
        if (countryID.equals("sy"))
            flag.setImageResource(com.blongho.country_data.R.drawable.sy);
        if (countryID.equals("tw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tw);
        if (countryID.equals("tj"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tj);
        if (countryID.equals("tz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tz);
        if (countryID.equals("th"))
            flag.setImageResource(com.blongho.country_data.R.drawable.th);
        if (countryID.equals("tg"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tg);
        if (countryID.equals("tk"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tk);
        if (countryID.equals("to"))
            flag.setImageResource(com.blongho.country_data.R.drawable.to);
        if (countryID.equals("tn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tn);
        if (countryID.equals("tr"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tr);
        if (countryID.equals("tm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tm);
        if (countryID.equals("tv"))
            flag.setImageResource(com.blongho.country_data.R.drawable.tv);
        if (countryID.equals("ae"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ae);
        if (countryID.equals("ug"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ug);
        if (countryID.equals("gb"))
            flag.setImageResource(com.blongho.country_data.R.drawable.gb);
        if (countryID.equals("ua"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ua);
        if (countryID.equals("uy"))
            flag.setImageResource(com.blongho.country_data.R.drawable.uy);
        if (countryID.equals("us"))
            flag.setImageResource(com.blongho.country_data.R.drawable.us);
        if (countryID.equals("uz"))
            flag.setImageResource(com.blongho.country_data.R.drawable.uz);
        if (countryID.equals("vu"))
            flag.setImageResource(com.blongho.country_data.R.drawable.vu);
        if (countryID.equals("va"))
            flag.setImageResource(com.blongho.country_data.R.drawable.va);
        if (countryID.equals("ve"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ve);
        if (countryID.equals("vn"))
            flag.setImageResource(com.blongho.country_data.R.drawable.vn);
        if (countryID.equals("wf"))
            flag.setImageResource(com.blongho.country_data.R.drawable.wf);
        if (countryID.equals("ye"))
            flag.setImageResource(com.blongho.country_data.R.drawable.ye);
        if (countryID.equals("zm"))
            flag.setImageResource(com.blongho.country_data.R.drawable.zm);
        if (countryID.equals("zw"))
            flag.setImageResource(com.blongho.country_data.R.drawable.zw);
    }
}
