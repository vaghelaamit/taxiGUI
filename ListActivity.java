package com.example.amit.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Amit on 3/31/2017.
 */

public class ListActivity extends AppCompatActivity {

	ListView listView;
	ListViewAdapter listViewAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.listview);

		listView = (ListView) findViewById(R.id.listView);

		MyVolley.init(this);

		if (checkInternetConnection()) {
			new LongOperation().execute();
		} else {
			Toast.makeText(this, "no internet", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		String message = "";
		ProgressDialog progressDialog;


		@Override
		protected void onPreExecute() {

			progressDialog = new ProgressDialog(ListActivity.this);
			progressDialog.setMessage(ListActivity.this.getString(R.string.json_wait_msg));
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

		@Override
		protected void onPreExecute() {

			progressDialog = new ProgressDialog(ListActivity.this);
			progressDialog.setMessage(ListActivity.this.getString(R.string.json_wait_msg));
			progressDialog.setCancelable(false);
			progressDialog.show();
		}


		@Override
		protected String doInBackground(String... params) {

			final String param0 = SyncConstant.URLJ + "/" + SyncConstant.MASTER_REQ_ALLDR;
			final String param1 = "fk_EmpGlCode=" + fk_EmpGlCode
					+ "&varClientName=" + client_Name;
			final String param2 = "HQWeekly";

			Utils.printLoge(5, "param0", "-->" + param0);
			Utils.printLoge(5, "param1", "-->" + param1);
			Utils.printLoge(5, "param2", "-->" + param2);

			String result = Utils.CallHttpMethod(ListActivity.this, param0, param1);
			Utils.printLoge(5, "result", "-->" + result);
			if (result != null && !result.equalsIgnoreCase("")) {
				try {
					boolean validCount = false;
					JSONObject jsonObject = new JSONObject(result);
					JSONArray jsonArray = jsonObject.optJSONArray("Status");
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObjects = jsonArray.optJSONObject(i);
							validCount = jsonObjects.optBoolean("isValid");
							message = jsonObjects.optString("Message");
						}

						if (validCount) {
							JSONArray DetailsArray = jsonObject.optJSONArray("Details");
							if (DetailsArray != null) {
								if (ListItems.size() > 0)
									ListItems.clear();

								if (tempListItems.size() > 0)
									tempListItems.clear();
								for (int k = 0; k < DetailsArray.length(); k++) {
									JSONObject ob = DetailsArray.getJSONObject(k);

									String varStatus = "";
									if (!ob.isNull("varStatus")) {
										varStatus = ob.getString("varStatus");
									}

									//if (varStatus.equalsIgnoreCase(RightFragment_MasterAddDr.SELECTION_USER)) {

									String varDrName = "";
									if (!ob.isNull("varDrName")) {
										varDrName = ob.getString("varDrName");
									}

									String varCategory = "";
									if (!ob.isNull("varCategory")) {
										varCategory = ob.getString("varCategory");
									}

									String varSpeciality = "";
									if (!ob.isNull("varSpeciality")) {
										varSpeciality = ob.getString("varSpeciality");
									}


									String varCity = "";
									if (!ob.isNull("varCity")) {
										varCity = ob.getString("varCity");
									}


									String varReqDate = "";
									if (!ob.isNull("varReqDate")) {
										varReqDate = ob.getString("varReqDate");
									}


									String varAppDate = "";
									if (!ob.isNull("varAppDate")) {
										varAppDate = ob.getString("varAppDate");
									}

									String varLatestStatus = "";
									if (!ob.isNull("varLatestStatus")) {
										varLatestStatus = ob.getString("varLatestStatus");
									}

									String varStatusUpdatedBy = "";
									if (!ob.isNull("varStatusUpdatedBy")) {
										varStatusUpdatedBy = ob.getString("varStatusUpdatedBy");
									}

									String varEmpCode = "";
									if (!ob.isNull("varEmpCode")) {
										varEmpCode = ob.getString("varEmpCode");
									}

									String varEmpName = "";
									if (!ob.isNull("varEmpName")) {
										varEmpName = ob.getString("varEmpName");
									}

									String varReqRemarks = "";
									if (!ob.isNull("varReqRemarks")) {
										varReqRemarks = ob.getString("varReqRemarks");
									}

									String varAppRemarks = "";
									if (!ob.isNull("varAppRemarks")) {
										varAppRemarks = ob.getString("varAppRemarks");
									}

									ModelMasterDrlist drlist = new ModelMasterDrlist();
									drlist.setVarDrName(varDrName);
									drlist.setVarCategory(varCategory);
									drlist.setVarSpeciality(varSpeciality);
									drlist.setVarCity(varCity);
									drlist.setVarReqDate(varReqDate);
									drlist.setVarStatus(varStatus);
									drlist.setVarAppDate(varAppDate);
									drlist.setVarLatestStatus(varLatestStatus);
									drlist.setVarStatusUpdatedBy(varStatusUpdatedBy);
									drlist.setVarEmpCode(varEmpCode);
									drlist.setVarEmpName(varEmpName);
									drlist.setVarReqRemarks(varReqRemarks);
									drlist.setVarAppRemarks(varAppRemarks);
									ListItems.add(drlist);
									tempListItems.add(drlist);
									//}
								}
							}
							return "TRUE";
						} else {
							return "MESSAGE";
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

			return "ERROR";
		}

		@Override
		protected void onPostExecute(String result) {

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			if (result.equalsIgnoreCase("ERROR")) {
				//Crouton.makeText(getActivity(), MessageConstant.MsgTryAgain, Style.ALERT).show();
			}
			if (result.equalsIgnoreCase("MESSAGE")) {
				//Crouton.makeText(getActivity(), message, Style.ALERT).show();
			} else {
				setDetailListLiew();
			}
		}
	}

	private void setDetailListLiew() {
		if (tempListItems.size() > 0) {
			listAdapter = new ListViewAdapter(getActivity(), tempListItems);
			drListView.setAdapter(listAdapter);
			noData.setVisibility(View.GONE);
			drListView.setVisibility(View.VISIBLE);
		} else {
			noData.setVisibility(View.VISIBLE);
			drListView.setVisibility(View.GONE);
		}
	}

	public class ListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private ArrayList<ModelMasterDrlist> arrList;

		public ListViewAdapter(Context con, ArrayList<ModelMasterDrlist> arrList1) {
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(con);
			this.arrList = arrList1;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return tempListItems.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			// return product_id1.size();
			return tempListItems.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			// return product_id1.get(position).hashCode();
			return tempListItems.get(position).hashCode();
		}

		public View getView(final int position, View convertView,
		                    ViewGroup parent) {
			// TODO Auto-generated method stub
			final ListContent holder;
			View v = convertView;
			if (v == null) {
				v = mInflater.inflate(R.layout.row_master_drlist_item, null);
				holder = new ListContent();

				holder.txtTitle = (TextView) v.findViewById(R.id.txtTitle);
				holder.txtDetails = (TextView) v.findViewById(R.id.txtDetails);
				holder.txtDate = (TextView) v.findViewById(R.id.txtDate);
				holder.imgFlag = (ImageView) v.findViewById(R.id.imgFlag);
				holder.line_row = (LinearLayout) v.findViewById(R.id.line_row);

				v.setTag(holder);
			} else {

				holder = (ListContent) v.getTag();
			}

			Log.e("STATUS", "--->" + tempListItems.get(position).getVarStatus());
			if (tempListItems.get(position).getVarStatus().equalsIgnoreCase("P")) {
				holder.txtDate.setText("Pending-" + tempListItems.get(position).getVarReqDate());
				if (ECPApplication.isLollipop()) {
					holder.imgFlag.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_action_alert_error, null));
				} else {
					holder.imgFlag.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_alert_error));
				}
			}

			holder.txtTitle.setText(tempListItems.get(position).getVarDrName());

			holder.txtDetails.setText(Details);
			holder.line_row.setVisibility(View.VISIBLE);
			//} else {
			//holder.line_row.setVisibility(View.GONE);
			//}

			//holder.line_row.setOnClickListener(mOnTitleClickListener);

			return v;
		}

		public Filter getFilter() {
			return new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
				                              FilterResults results) {
					tempListItems = (ArrayList<ModelMasterDrlist>) results.values;
					ListViewAdapter.this.notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<ModelMasterDrlist> filteredArrList = new ArrayList<>();
					if (ListViewAdapter.this.arrList == null) {
						ListViewAdapter.this.arrList = new ArrayList<>(tempListItems);
					}

					if (constraint == null || constraint.length() == 0) {
						results.count = ListViewAdapter.this.arrList.size();
						results.values = ListViewAdapter.this.arrList;
					} else {
						constraint = constraint.toString().toLowerCase().trim();
						final int arrayListSize = ListViewAdapter.this.arrList.size();
						for (int i = 0; i < arrayListSize; i++) {
							String data = ListViewAdapter.this.arrList.get(i).getVarDrName();
							if (data.toLowerCase()
									.startsWith(constraint.toString())) {
								filteredArrList.add(ListViewAdapter.this.arrList.get(i));
							}
						}
						results.count = filteredArrList.size();
						results.values = filteredArrList;
					}
					return results;
				}
			};
		}
	}

	static class ListContent {
		ImageView imgFlag;
		TextView txtTitle, txtDetails, txtDate;
		LinearLayout line_row;
	}
}
