package vcs.com.demoall;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vcsdev0103 on 31-03-2017.
 */

public class ListviewPagination extends AppCompatActivity {

   RecyclerView listView;
   TextView noData;
   ArrayList<CityModel> nwModels = new ArrayList<>();
   String URLJ = "http://172.16.8.105/ECPIOSMobileWebService_Ver180/ecpMobileToWebSync.asmx";
   Map<String, String> parameterPost = new HashMap<>();
   RecyclerListAdapter adapter = null;
   Button btnUp, btnDown;
   Horizontal_List_view h_list;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.listviewpage);

      ActionBar actionBar = getSupportActionBar();
      assert actionBar != null;
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowCustomEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_action_hardware_keyboard_arrow_left);
      actionBar.setCustomView(R.layout.actionbar);
      ((TextView) findViewById(R.id.actionTitle)).setText("NWDay");

      listView = (RecyclerView) findViewById(R.id.listView);
      noData = (TextView) findViewById(R.id.noData);
      h_list = (Horizontal_List_view) findViewById(R.id.HO_Order_sku_Fragment_pageView);

      RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ListviewPagination.this);
      listView.setHasFixedSize(true);
      listView.setLayoutManager(mLayoutManager);
      listView.setItemAnimator(new DefaultItemAnimator());

      if (isNetworkConnected()) {
         new Longoperation().execute();
      } else {
         Toast.makeText(ListviewPagination.this, "no internet connection", Toast.LENGTH_SHORT).show();
      }

      btnUp = (Button) findViewById(R.id.btnUp);
      btnUp.setVisibility(View.GONE);
      btnUp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            //if (nwModels.size() > 0)
            //   listView.smoothScrollToPosition(0);
         }
      });

      btnDown = (Button) findViewById(R.id.btnDown);
      btnDown.setVisibility(View.GONE);
      btnDown.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            //if (nwModels.size() > 0)
            //   listView.smoothScrollToPosition(0);
         }
      });

      listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
         }

         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
               btnDown.setVisibility(View.GONE);
               btnUp.setVisibility(View.GONE);
            } else if (dy < 0) {
               btnDown.setVisibility(View.VISIBLE);
               btnUp.setVisibility(View.VISIBLE);
            }
         }
      });

      /*h_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            pagecount = position + 1;
            data.clear();
            for (int i = 1; i <= limit; i++) {
               PageModel m = new PageModel();
               m.setPage(i);
               m.setIsSelected(false);
               data.add(m);
            }
            data.get(position).setIsSelected(true);
            ad.notifyDataSetChanged();
            Get_Order_Approach();
         }
      });

      //json parsing

      if (PageFlag) {
         total_rows = jo.getString("total_rows");
         PageFlag = false;
         limit = Integer.parseInt(jo.getString("pages"));
         data = new ArrayList<PageModel>();
         for (int i = 1; i <= limit; i++) {
            PageModel m = new PageModel();
            m.setPage(i);
            m.setIsSelected(false);
            data.add(m);
         }
         data.get(0).setIsSelected(true);
         ad = new PagerAdapter(getActivity(), data);
         h_list.setAdapter(ad);
      }  */
   }

   /*@Override
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
      if (id == R.id.action_home) {
         Intent intent = new Intent(NwDayActivity.this, DashBoardActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(intent);
         return true;
      } else if (id == android.R.id.home) {
         onBackPressed();
         return true;
      } else if (id == R.id.action_logout) {
         Intent intent = new Intent(NwDayActivity.this, Login.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(intent);
         return true;
      } else {
         return super.onOptionsItemSelected(item);
      }
   }

   @Override
   public void onBackPressed() {
      super.onBackPressed();
      finish();
   }*/

   private boolean isNetworkConnected() {
      ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      return cm.getActiveNetworkInfo() != null;
   }

   public class Longoperation extends AsyncTask<String, Void, String> {

      private ProgressDialog progressDialog;
      private String message = "";

      @Override
      protected void onPreExecute() {
         progressDialog = new ProgressDialog(ListviewPagination.this);
         progressDialog.setMessage("Please wait...");
         progressDialog.setCancelable(false);
         progressDialog.show();
      }

      @Override
      protected String doInBackground(String... params) {

         //String fkStateGlCode = params[0];

         final String param0 = URLJ + "/" + "OM_IOSNW_GetCityList";
         final String param1 = "fk_EmpGLCode_Login=" + 252 +
           "&varClientName=" + "IQAA" + "&fk_StateGlCode=" + 1;

         parameterPost.clear();
         parameterPost.put("fk_EmpGLCode_Login", "" + 252);
         parameterPost.put("varClientName", "" + "IQAA");
         parameterPost.put("fk_StateGlCode", "" + 1);

         Utils.printLoge(5, "param0", "*****" + param0);
         Utils.printLoge(5, "param1", "*****" + param1);
         String result = Utils.CallHttpMethod(ListviewPagination.this, param0, param1);

         if (!result.equalsIgnoreCase("")) {
            boolean validCount = false;
            try {
               JSONObject jsonObject = new JSONObject(result);
               JSONArray jsonArray = jsonObject.optJSONArray("Status");
               if (jsonArray != null) {
                  for (int i = 0; i < jsonArray.length(); i++) {
                     JSONObject jsonObjects = jsonArray.optJSONObject(i);
                     validCount = jsonObjects.optBoolean("isValid");
                     message = jsonObjects.optString("Message");
                  }
                  if (validCount) {
                     JSONArray DetailsjsonArray = jsonObject.optJSONArray("LstCity_Mst");
                     if (DetailsjsonArray != null) {
                        /*cityModels.clear();
                        nwCity.clear();
                        CityModel nm1 = new CityModel();
                        nm1.setFk_StateGlCode(0);
                        nm1.setIntGlCode(0);
                        nm1.setVarCityName("Select City");
                        nwCity.add("Select City");
                        cityModels.add(nm1);*/
                        for (int i = 0; i < DetailsjsonArray.length(); i++) {
                           JSONObject _jsonObjects = DetailsjsonArray.optJSONObject(i);

                           int fk_StateGlCode = (!_jsonObjects.isNull("fk_StateGlCode")) ?
                             _jsonObjects.getInt("fk_StateGlCode") : 0;
                           int intGlCode = (!_jsonObjects.isNull("intGlCode")) ?
                             _jsonObjects.getInt("intGlCode") : 0;
                           String varCityName = (!_jsonObjects.isNull("varCityName")) ? _jsonObjects
                             .getString("varCityName") : "";

                           CityModel nm = new CityModel();
                           nm.setFk_StateGlCode(fk_StateGlCode);
                           nm.setIntGlCode(intGlCode);
                           nm.setVarCityName(varCityName);
                           //nwCity.add(varCityName);
                           nwModels.add(nm);
                        }

                        return "DONE";
                     } else {
                        return "ERROR";
                     }
                  } else {
                     return "ERROR";
                  }
               } else {
                  return "ERROR";
               }
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
            if (nwModels != null && nwModels.size() > 0) {
               listView.setVisibility(View.VISIBLE);
               noData.setVisibility(View.GONE);
               adapter = new RecyclerListAdapter();
               listView.setAdapter(new RecyclerListAdapter());
            } else {
               listView.setVisibility(View.GONE);
               noData.setVisibility(View.VISIBLE);
            }
         } else if (result.equalsIgnoreCase("ERROR")) {
            listView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
         }
      }
   }

   public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ViewHolder> {

      @Override
      public RecyclerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listnwday_new,
                                                                      parent, false);
         ViewHolder viewHolder = new ViewHolder(view);

         return viewHolder;
      }

      @Override
      public void onBindViewHolder(RecyclerListAdapter.ViewHolder holder, int position) {
         holder.txtDoctorName.setText(nwModels.get(position).getVarCityName());
         //holder.txtCategory.setText(nwModels.get(position).getFk_StateGlCode());
      }

      @Override
      public int getItemCount() {
         return nwModels.size();
      }

      public class ViewHolder extends RecyclerView.ViewHolder {
         // each data item is just a string in this case
         private TextView txtCategory, txtDoctorName;

         public ViewHolder(View view) {
            super(view);
            //txtCategory = (TextView) view.findViewById(R.id.dataNw);
            txtDoctorName = (TextView) view.findViewById(R.id.dataCity);

            view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  //getAdapterPosition()
                  //Intent intentDetails = new Intent(getActivity(), DoctorAnalysisDetailsActivity.class);
                  //startActivity(intentDetails);
               }
            });
         }
      }
   }

   /*public class ListDataAdapter extends BaseAdapter {

      private final Activity activity;
      private LayoutInflater inflater = null;

      public ListDataAdapter(Activity a) {
         activity = a;
         inflater = (LayoutInflater) activity
           .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      }

      @Override
      public int getCount() {
         return nwModels.size();
      }

      @Override
      public Object getItem(int position) {
         return position;
      }

      @Override
      public long getItemId(int position) {
         return position;
      }

      @Override
      public View getView(final int position, View view, ViewGroup parent) {

         View v = view;
         if (v == null) v = inflater.inflate(R.layout.activity_listnwday_new, parent, false);

         TextView dataNw = (TextView) v.findViewById(R.id.dataNw);
         TextView dataCity = (TextView) v.findViewById(R.id.dataCity);
         TextView dataRemarks = (TextView) v.findViewById(R.id.dataRemarks);
         ImageView dataImage = (ImageView) v.findViewById(R.id.dataImage);

         final CityModel nw = nwModels.get(position);
         dataNw.setText(nw.getVarCityName());
         //dataRemarks.setText(nw.getFk_StateGlCode());

         dataImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(ListviewPagination.this, LinkedlistActivity.class);
               startActivity(i);
            }
         });

         *//*View.OnClickListener onChk = new View.OnClickListener() {
            public void onClick(View v) {

               int index = listView.getFirstVisiblePosition();
               getListView().smoothScrollToPosition(index + 1); // For increment.

            }
         };*//*

         return v;
      }
   }*/
}
