package id.sakuvo.app;
import android.animation.*;import android.app.Activity;import android.content.Intent;import android.graphics.Typeface;import android.net.Uri;import android.os.*;import android.view.*;import android.view.animation.*;import android.webkit.*;import android.widget.*;import java.io.*;import java.util.*;import org.json.JSONObject;
public class MainActivity extends Activity {
  static final int BG=0xFF0A4AA6,INK=0xFFFFFFFF,AC=0xFFFFB81C;
  WebView web;FrameLayout root,splash;Handler hd=new Handler();boolean ready,minDone,gone;long stopAt;String pName,pMime,pText;List<Animator> loops=new ArrayList<Animator>();
  int dp(float v){return Math.round(v*getResources().getDisplayMetrics().density);}
  LinearLayout.LayoutParams lp(int w,int h,int top){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w,h);p.topMargin=dp(top);return p;}
  public class Bridge{
    @JavascriptInterface public void save(String n,String m,String t){pName=n;pMime=m;pText=t;runOnUiThread(new Runnable(){public void run(){Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType(pMime);i.putExtra(Intent.EXTRA_TITLE,pName);startActivityForResult(i,1);}});}
    @JavascriptInterface public void pick(){runOnUiThread(new Runnable(){public void run(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");startActivityForResult(i,2);}});}
  }
  @Override public void onCreate(Bundle st){
    super.onCreate(st);
    root=new FrameLayout(this);root.setBackgroundColor(BG);
    web=new WebView(this);web.setBackgroundColor(0xFFF2F5FA);root.addView(web,new FrameLayout.LayoutParams(-1,-1));
    buildSplash();root.addView(splash,new FrameLayout.LayoutParams(-1,-1));setContentView(root);
    web.setWebViewClient(new WebViewClient(){@Override public void onPageFinished(WebView v,String u){ready=true;tryHide();}});
    web.setWebChromeClient(new WebChromeClient());
    WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(true);s.setAllowFileAccessFromFileURLs(false);s.setAllowUniversalAccessFromFileURLs(false);
    web.addJavascriptInterface(new Bridge(),"Android");web.loadUrl("file:///android_asset/index.html");
    hd.postDelayed(new Runnable(){public void run(){minDone=true;tryHide();}},1700);
    hd.postDelayed(new Runnable(){public void run(){ready=true;minDone=true;tryHide();}},6000);
  }
  void buildSplash(){
    splash=new FrameLayout(this);splash.setBackgroundColor(BG);splash.setClickable(true);
    LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);splash.addView(box,new FrameLayout.LayoutParams(-1,-1));
    ImageView logo=new ImageView(this);logo.setImageResource(R.drawable.splash_logo);box.addView(logo,lp(dp(96),dp(96),0));
    logo.setAlpha(0f);logo.setScaleX(.7f);logo.setScaleY(.7f);logo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
    TextView t=new TextView(this);t.setText("sakuvo");t.setTextColor(INK);t.setTextSize(30);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);box.addView(t,lp(-2,-2,20));
    t.setAlpha(0f);t.setTranslationY(dp(14));t.animate().alpha(1f).translationY(0).setStartDelay(250).setDuration(500).start();
    TextView s=new TextView(this);s.setText("catat uang, tetap di HP-mu");s.setTextColor(0xFFBFD3F5);s.setTextSize(13);box.addView(s,lp(-2,-2,6));
    s.setAlpha(0f);s.animate().alpha(1f).setStartDelay(450).setDuration(500).start();
    FrameLayout track=new FrameLayout(this);track.setBackgroundColor(0x33FFFFFF);box.addView(track,lp(dp(120),dp(3),40));
    View bar=new View(this);bar.setBackgroundColor(AC);track.addView(bar,new FrameLayout.LayoutParams(dp(44),-1));
    ObjectAnimator a=ObjectAnimator.ofFloat(bar,"translationX",0f,dp(76));a.setDuration(900);a.setRepeatCount(ObjectAnimator.INFINITE);a.setRepeatMode(ObjectAnimator.REVERSE);a.setInterpolator(new AccelerateDecelerateInterpolator());a.start();loops.add(a);
  }
  void tryHide(){
    if(gone||!ready||!minDone)return;gone=true;
    splash.animate().alpha(0f).setDuration(380).withEndAction(new Runnable(){public void run(){for(Animator a:loops)a.cancel();loops.clear();root.removeView(splash);}}).start();
  }
  @Override protected void onActivityResult(int rq,int rs,Intent d){
    if(rs!=RESULT_OK||d==null||d.getData()==null)return;
    try{Uri u=d.getData();
      if(rq==1){OutputStream o=getContentResolver().openOutputStream(u);o.write(pText.getBytes("UTF-8"));o.close();Toast.makeText(this,"Tersimpan",Toast.LENGTH_SHORT).show();}
      else if(rq==2){InputStream in=getContentResolver().openInputStream(u);ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;while((n=in.read(buf))>0)b.write(buf,0,n);in.close();web.evaluateJavascript("onFile("+JSONObject.quote(b.toString("UTF-8"))+")",null);}
    }catch(Exception e){Toast.makeText(this,"Gagal: "+e.getMessage(),Toast.LENGTH_LONG).show();}
  }
  @Override protected void onStop(){stopAt=SystemClock.elapsedRealtime();super.onStop();}
  @Override protected void onStart(){super.onStart();if(stopAt>0&&SystemClock.elapsedRealtime()-stopAt>30000)web.evaluateJavascript("window.lockNow&&lockNow()",null);}
  @Override protected void onDestroy(){hd.removeCallbacksAndMessages(null);for(Animator a:loops)a.cancel();super.onDestroy();}
  @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
