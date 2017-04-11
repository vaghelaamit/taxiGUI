package vcs.com.demoall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vcs.com.demoall.model.Student;

public class ArraylistActivity extends SetDataAbstract implements
                                                       SetData,
                                                       android.location.LocationListener {

   //GoogleApiClient mGoogleApiClient;
   LocationManager locationManager;
   String provider = "";
   TextView lat, longi;
   boolean isPermission = false;
   //RecyclerView listView;
   //TextView noData;
   //ArrayList<CityModel> nwModels = new ArrayList<>();
   String URLJ = "http://172.16.8.105/ECPIOSMobileWebService_Ver180/ecpMobileToWebSync.asmx";
   Map<String, String> parameterPost = new HashMap<>();
   ListView listView;
   TextView noData;
   boolean flag_loading = false;
   RecyclerListAdapter adapter;
   boolean loadMore = false;
   int pre = 0, next = 0;
   List<CityModel> ctListArr = new ArrayList<>();
   List<CityModel> page = new ArrayList<>();
   /*@Override
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.fab:
            break;
         default:
      }
   }*/
   int currentPage = 0;
   private ArrayList<String> strArr = new ArrayList<>();
   private ArrayList<String> strArrNew = new ArrayList<>();
   private ArrayList<Student> arrStdModel = new ArrayList<>();
   private ArrayList<CityModel> ctArr = new ArrayList<>();
   //RecyclerListAdapter adapter = null;
   //Button btnUp, btnDown;
   private ArrayList<CityModel> ctArr10 = new ArrayList<>();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.listviewpage);

      /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);*/
      listView = (ListView) findViewById(R.id.listView);
      noData = (TextView) findViewById(R.id.noData);
      jsonParse();

      listView.setOnScrollListener(new AbsListView.OnScrollListener() {
         float mInitialX = 0, mInitialY = 0;
         boolean scrollDown = false;
         private int currentVisibleItemCount;
         private int currentScrollState;
         private int currentFirstVisibleItem;
         private int totalItem;
         private int mLastFirstVisibleItem;

         @Override
         public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub
            this.currentScrollState = scrollState;
            this.isScrollCompleted();

            view.setOnTouchListener(new View.OnTouchListener() {
               @Override
               public boolean onTouch(View v, MotionEvent event) {
                  switch (event.getAction()) {
                     case MotionEvent.ACTION_DOWN:
                        mInitialX = event.getX();
                        mInitialY = event.getY();
                        return true;
                     case MotionEvent.ACTION_MOVE:
                        final float x = event.getX();
                        final float y = event.getY();
                        final float yDiff = y - mInitialY;
                        if (yDiff > 0.0) {
                           Log.d("", "SCROLL DOWN");
                           scrollDown = true;
                           currentPage++;
                           break;
                        } else if (yDiff < 0.0) {
                           Log.d("", "SCROLL up");
                           scrollDown = true;
                           if (currentPage > 0) {
                              currentPage--;
                           }
                           break;
                        }
                        break;
                  }
                  return false;
               }
            });
         }

         private void isScrollCompleted() {
            if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
              && this.currentScrollState == SCROLL_STATE_IDLE) {

               new Longoperation().execute("" + currentPage);
            }
         }

         @Override
         public void onScroll(AbsListView view, int firstVisibleItem,
                              int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            this.currentFirstVisibleItem = firstVisibleItem;
            this.currentVisibleItemCount = visibleItemCount;
            this.totalItem = totalItemCount;
            if (mLastFirstVisibleItem < firstVisibleItem) {
               Log.i("SCROLLING DOWN", "TRUE");
            }
            if (mLastFirstVisibleItem > firstVisibleItem) {
               Log.i("SCROLLING UP", "TRUE");
            }
            mLastFirstVisibleItem = firstVisibleItem;
         }
      });



      /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      lat = (TextView) findViewById(R.id.lat);
      longi = (TextView) findViewById(R.id.longi);

      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

      Criteria criteria = new Criteria();
      provider = locationManager.getBestProvider(criteria, false);
      Location location = null;

      if (Build.VERSION.SDK_INT >= 23) {
         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
           != PackageManager.PERMISSION_GRANTED
           || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
           != PackageManager.PERMISSION_GRANTED) {
            isPermission = false;
         } else {
            isPermission = true;
         }
      }*/

      /*if (isPermission) {
         location = locationManager.getLastKnownLocation(provider);
      } else {
         AlertDialog.Builder builder =
           new AlertDialog.Builder(this);
         builder.setTitle(getResources().getString(R.string.app_name));
         builder.setMessage("allow app to access to your details. Tap settings > Permission, and turn on " +
                              "all");
         builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                          Uri.fromParts("package", getPackageName(), null));
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
               isPermission = true;
            }
         });
         builder.setNegativeButton("Cancel", null);
         builder.setCancelable(false);
         builder.show();
      }

      if (location != null) {
         Log.e("Provider ", " has been selected." + provider);
         onLocationChanged(location);
      } else {
         lat.setText("Location not available");
         longi.setText("Location not available");
      }

      strArr.add("A");
      strArr.add("B");
      strArr.add("C");
      strArr.add("D");

      strArrNew.add("A");
      strArrNew.add("B");

      Student s1 = new Student(1, "a", 25);
      Student s2 = new Student(2, "b", 26);
      Student s3 = new Student(3, "c", 27);

      arrStdModel.add(s1);
      arrStdModel.add(s2);
      arrStdModel.add(s3);

      strArr.removeAll(strArrNew);

      fab.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            Iterator itr = arrStdModel.iterator();
            while (itr.hasNext()) {
               Student st = (Student) itr.next();
               Log.e("iterate", "---" + st.intGlCode + " " + st.name + " " + st.age);
            }

            for (int j = 0; j < arrStdModel.size(); j++) {
               Log.e("std", "for-->>" + arrStdModel.get(j).intGlCode);
               Log.e("std", "for-->>" + arrStdModel.get(j).age);
               Log.e("std", "for-->>" + arrStdModel.get(j).name);
            }

            for (int i = 0; i < strArr.size(); i++) {
               Log.e("str", "for-->>" + strArr.toString());
            }

            *//*Intent i = new Intent(ArraylistActivity.this, LinkedlistActivity.class);
            startActivity(i);*//*
            Intent i = new Intent(ArraylistActivity.this, LocationService.class);
            startService(i);
            *//*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();*//*
         }
      });*/
   }

   private void jsonParse() {
      new Longoperation().execute("" + currentPage);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
   }

   @Override
   public void onLocationChanged(Location location) {
      provider = location.getProvider();
      lat.setText("" + location.getLatitude());
      longi.setText("" + location.getLongitude());
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public void setData() {
      ShowLogs();
   }

   private void ShowLogs() {
      Log.e("setDAta", "1232114546456");
   }

   @Override
   public int getNumberValue() {
      return 1;
   }

   @Override
   public String setVisibility() {
      return "Y";
   }

   @Override
   public void setDataAbstract() {
      Log.e("setDAtaAB", "1232114546456");
   }

   @Override
   public void onStatusChanged(String s, int i, Bundle bundle) {
      Log.e("onStatusChanged", "1232114546456");
   }

   @Override
   public void onProviderEnabled(String s) {
      Log.e("onProviderEnabled", "1232114546456");
   }

   @Override
   public void onProviderDisabled(String s) {
      Log.e("onProviderDisabled", "1232114546456");
   }

   @Override
   protected void onResume() {
      super.onResume();
      //if (isPermission)
      ////   locationManager.requestLocationUpdates(provider, 400, 1, ArraylistActivity.this);
   }

   @Override
   protected void onPause() {
      super.onPause();
      // if (isPermission) {
      //   locationManager.removeUpdates(ArraylistActivity.this);
      //}
   }

   public class Longoperation extends AsyncTask<String, Void, String> {

      int values = 0;

      private ProgressDialog progressDialog;
      private String message = "";

      @Override
      protected void onPreExecute() {
         progressDialog = new ProgressDialog(ArraylistActivity.this);
         progressDialog.setMessage("Please wait...");
         progressDialog.setCancelable(false);
         progressDialog.show();
      }

      @Override
      protected String doInBackground(String... params) {

         currentPage = Integer.parseInt(params[0]);
         //String fkStateGlCode = params[0];

         /*final String param0 = URLJ + "/" + "OM_IOSNW_GetCityList";
         final String param1 = "fk_EmpGLCode_Login=" + 252 +
           "&varClientName=" + "IQAA" + "&fk_StateGlCode=" + 1;
         parameterPost.clear();
         parameterPost.put("fk_EmpGLCode_Login", "" + 252);
         parameterPost.put("varClientName", "" + "IQAA");
         parameterPost.put("fk_StateGlCode", "" + 1);
         Utils.printLoge(5, "param0", "*****" + param0);
         Utils.printLoge(5, "param1", "*****" + param1);*/
         //String result = Utils.CallHttpMethod(JsonParsing.this, param0, param1);
         String result = "{\n" +
           "  name: Alice,\n" +
           "  age: 20,\n" +
           "  address: {\n" +
           "    streetAddress: 100WallStreet,\n" +
           "    city: NewYork\n" +
           "  },\n" +
           "  phoneNumber: [\n" +
           "    {\n" +
           "      type: home1,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home2,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home3,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home4,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home5,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home6,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home7,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home8,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home9,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home10,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home11,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home12,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home13,\n" +
           "      number: 212-333-1111\n" +
           "    },\n" +
           "    {\n" +
           "      type: home14,\n" +
           "      number: 212-333-1111\n" +
           "    }\n" +
           "  ]\n" +
           "}";

         if (!result.equalsIgnoreCase("")) {
            try {
               ctArr.clear();
               ctArr10.clear();
               JSONObject jsonObject = new JSONObject(result);
               String name = jsonObject.optString("name");
               String age = jsonObject.optString("age");
               JSONObject jo1 = jsonObject.optJSONObject("address");
               String streetAddress = jo1.optString("streetAddress");
               String city = jo1.optString("city");
               JSONArray jo2 = jsonObject.optJSONArray("phoneNumber");
               for (int k = 0; k < jo2.length(); k++) {
                  JSONObject jo3 = jo2.optJSONObject(k);
                  String type = jo3.optString("type");
                  String number = jo3.optString("number");
                  CityModel cm = new CityModel();
                  cm.setType(type);
                  cm.setNumber(number);
                  ctArr.add(cm);
               }

               //next = next + 5;
               ctArr10 = new ArrayList<>(ctArr.subList(pre, next));
               loadMore = true;

               Log.e("newList", "--" + ctArr10.size());

               return "DONE";
            } catch (Exception e) {
               e.printStackTrace();
               return "ERROR";
            }
         } else {
            return "ERROR";
         }
      }

      @Override
      protected void onPostExecute(String result) {
         if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
         }

         if (result.equalsIgnoreCase("DONE")) {
            if (ctArr10 != null && ctArr10.size() > 0) {
               listView.setVisibility(View.VISIBLE);
               noData.setVisibility(View.GONE);
               //RecyclerListAdapter adapter = new RecyclerListAdapter();
               if (adapter == null) {
                  listView.setAdapter(new RecyclerListAdapter(ArraylistActivity.this, ctArr10));
               } else {
                  adapter.notifyDataSetChanged();
               }
               //} else {
               //   listView.setVisibility(View.GONE);
               //   noData.setVisibility(View.VISIBLE);
               //}
            } else if (result.equalsIgnoreCase("ERROR")) {
               // listView.setVisibility(View.GONE);
               // noData.setVisibility(View.VISIBLE);
            }
         }
      }
   }

   public class RecyclerListAdapter extends BaseAdapter {

      private Activity activity;
      private LayoutInflater inflaer;

      public RecyclerListAdapter(ArraylistActivity activityOld, List<CityModel> ct) {
         activity = activityOld;
         page = ct;
         inflaer = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      }

      @Override
      public int getCount() {
         return ctArr10.size();
      }

      @Override
      public Object getItem(int i) {
         return i;
      }

      @Override
      public long getItemId(int i) {
         return i;
      }

      @Override
      public View getView(final int position, View view, ViewGroup parent) {

         View v = view;
         if (v == null) v = inflaer.inflate(R.layout.activity_listnwday_new, parent, false);

         TextView dataNw = (TextView) v.findViewById(R.id.dataNw);
         TextView dataCity = (TextView) v.findViewById(R.id.dataCity);
         TextView dataRemarks = (TextView) v.findViewById(R.id.dataRemarks);
         ImageView dataImage = (ImageView) v.findViewById(R.id.dataImage);

         final CityModel nw = page.get(position);
         dataNw.setText(nw.getType());
         dataCity.setText(nw.getNumber());

         return v;
      }
   }

   /*public class EndlessScrollListener implements AbsListView.OnScrollListener {
      private int visibleThreshold = 5;
      private int currentPage = 0;
      private int previousTotal = 0;
      private boolean loading = true;

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem,
                           int visibleItemCount, int totalItemCount) {
         if (loading) {
            if (totalItemCount > previousTotal) {
               loading = false;
               previousTotal = totalItemCount;
               currentPage++;
            }
         }
         if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            new Longoperation().execute("" + currentPage + 1);
            loading = true;
         }
      }

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
      }
   }*/
}
