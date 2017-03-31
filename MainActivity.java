package com.example.amit.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Fragment implements Filterable {

	//private DBAdapter db = null;
	public static ArrayList<clsList_Mst> Data_List = new ArrayList<>();
	private static ArrayList<clsList_Mst> Data_List_temp = new ArrayList<>();
	private TextView textView;
	private LinearLayout line_list;
	private ScrollView scrollView;
	public static ArrayList<clsList_Mst> Sample_Data1 = new ArrayList<>();
	boolean isFreeQtyVisible = false;
	EditText etSearch;
	String textChange = "";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//db = new DBAdapter(getActivity());


	}

	@Override
	public Filter getFilter() {
		return new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
			                              FilterResults results) {
				//DoctorFAdapter.this.displayArrList = (ArrayList<clsList_Mst>) results.values;
				//DoctorFAdapter.this.notifyDataSetChanged();

				for (int i = 0; i < Data_List.size(); i++) {
					if (Data_List.contains(textChange)) {
						clsList_Mst clsList_mst = new clsList_Mst();
						clsList_mst.setVarName(textChange);
						Data_List.add(clsList_mst);
					}
				}

				setUPListview();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				ArrayList<clsList_Mst> filteredArrList = new ArrayList<>();
				if (Data_List == null) {
					Data_List = new ArrayList<>(Data_List);
				}

				if (constraint == null || constraint.length() == 0) {
					results.count = Data_List.size();
					results.values = Data_List;
				} else {
					constraint = constraint.toString().toLowerCase().trim();
					final int arrayListSize = Data_List.size();
					for (int i = 0; i < arrayListSize; i++) {
						String data = "";

						if (data.toLowerCase()
								.startsWith(constraint.toString())) {
							filteredArrList.add(Data_List.get(i));
						}
					}
					results.count = filteredArrList.size();
					results.values = filteredArrList;
				}
				return results;
			}
		};
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_productlist_doc, container, false);

		textView = (TextView) rootView.findViewById(R.id.textView);
		line_list = (LinearLayout) rootView.findViewById(R.id.line_list);
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		TextView txtPOBVisitDate = (TextView) rootView.findViewById(R.id.txtPOBVisitDate);
		etSearch = (EditText) rootView.findViewById(R.id.etSearch);
		//txtPOBVisitDate.setText(" " + DoctorPobProductActivity.VarDrCode + " - " + DoctorPobProductActivity.drName + " : " + DoctorPobProductActivity.SelectDate);
		ImageButton imgbtnPOBVisitSave = (ImageButton) rootView.findViewById(R.id.imgbtnPOBVisitSave);
		imgbtnPOBVisitSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean isGoSave = false;
				if (Data_List.size() > 0) {
					int Size = Data_List.size();
					/*for (int i = 0; i < Size; i++) {
						if (Data_List.get(i).isChange() && Data_List.get(i).getIntQty() > 0) {
							isGoSave = true;
							break;
						}
					}*/
				}

				if (isGoSave) {
					Intent resultIntent = new Intent();
					getActivity().setResult(Activity.RESULT_OK, resultIntent);
					getActivity().finish();
				} else {
					//Utils.printLoge(5, "NotChange", "NotChange");
					Intent resultIntent = new Intent();
					getActivity().setResult(Activity.RESULT_OK, resultIntent);
					getActivity().finish();
				}
			}
		});

		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textChange = s.toString();

				getFilter().filter(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		return rootView;
	}

	public void refreshListItems() {

		scrollView.setVisibility(View.GONE);
		textView.setVisibility(View.VISIBLE);
		textView.setText("Please while setup products");

		new LongOperation().execute();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		refreshListItems();
	}

	private void GetListData() {
		if (Sample_Data1.size() > 0) {
			Data_List_temp.clear();
			Data_List_temp.addAll(Data_List);
		}
		Data_List.clear();

		clsList_Mst objList_Mst = new clsList_Mst();
		for (int i = 0; i < 2; i++) {
			objList_Mst.setVarName("add");
			Data_List.add(objList_Mst);
		}
	}


	private void setUPListview() {
		if (Data_List.size() > 0) {
			line_list.removeAllViews();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			int Size = Data_List.size();
			for (int i = 0; i < Size; i++) {
				final View row = inflater.inflate(R.layout.row_doc_secondary_product, null, false);
				final TextView textProductName = (TextView) row.findViewById(R.id.textProductName);
				final LinearLayout lineFree = (LinearLayout) row.findViewById(R.id.lineFree);
				final EditText editOrder = (EditText) row.findViewById(R.id.editOrder);
				final EditText editFree = (EditText) row.findViewById(R.id.editFree);
				row.setId(i);

				textProductName.setText("" + Data_List.get(i).getVarName());
				editOrder.setText("" + Data_List.get(i).getIntQty());

				editOrder.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						//Utils.printLoge(5, row.getId() + "-->", "-->" + s.toString());
						try {
							if (s.toString().length() > 0) {
								//Data_List.get(row.getId()).setIsChange(true);
								Data_List.get(row.getId()).setChecked(true);
							} else {
								//Data_List.get(row.getId()).setIsChange(false);
								Data_List.get(row.getId()).setChecked(false);
							}
							Data_List.get(row.getId()).setIntQty(Integer.parseInt(s.toString()));
						} catch (Exception e) {
							Data_List.get(row.getId()).setIntQty(0);
							e.printStackTrace();
							//Utils.printLoge(5, "error -->", "-->" + e.getMessage());
						}
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});

				if (isFreeQtyVisible) {
					lineFree.setVisibility(View.VISIBLE);
					editFree.setText("" + Data_List.get(i).getIntfreeQty());
					editFree.addTextChangedListener(new TextWatcher() {
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {

						}

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							//Utils.printLoge(5, row.getId() + "-->", "-->" + s.toString());
							try {
								if (s.toString().length() > 0) {
									//Data_List.get(row.getId()).setIsChange(true);
									Data_List.get(row.getId()).setChecked(true);
								} else {
									//Data_List.get(row.getId()).setIsChange(false);
									Data_List.get(row.getId()).setChecked(false);
								}
								Data_List.get(row.getId()).setIntfreeQty(Integer.parseInt(s.toString()));

							} catch (Exception e) {
								Data_List.get(row.getId()).setIntfreeQty(0);
								e.printStackTrace();
								//Utils.printLoge(5, "error -->", "-->" + e.getMessage());
							}
						}

						@Override
						public void afterTextChanged(Editable s) {

						}
					});
				} else {
					lineFree.setVisibility(View.GONE);
				}
				line_list.addView(row);
			}
		}
	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		private ProgressDialog progressDialog;

		@Override
		protected String doInBackground(String... params) {
			GetListData();
			return "done";
		}

		@Override
		protected void onPostExecute(String result) {

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			//TextView txt = (TextView) findViewById(R.id.output);
			//txt.setText("Executed"); // txt.setText(result);
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
			if (result.equalsIgnoreCase("Done")) {
				if (Data_List.size() > 0) {
					scrollView.setVisibility(View.VISIBLE);
					textView.setVisibility(View.GONE);
					setUPListview();
				} else {
					textView.setText("No data found");
					scrollView.setVisibility(View.GONE);
					textView.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("wait");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (Data_List_temp.size() > 0)
			Data_List_temp.clear();
	}
}

