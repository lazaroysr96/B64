package cu.base64.lysr;

import android.app.*;
import android.os.*;
import java.io.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import android.content.*;
import androidex.dialog.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import android.graphics.*;
import java.text.*;
import android.preference.*;
import android.graphics.drawable.*;
import android.net.*;
import android.util.*;
import android.provider.*;
import android.database.*;

public class MainActivity extends Activity 
{
	private File folder;
	private ImageButton 
	button_txt,
	button_save,
	button_preference,
	button_search,
	button_borrar,
	button_pegar,
	button_optimization;
	private TextView textview;
	private SharedPreferences shared;
	private Handler handler = new Handler();
	private ListView listview;
	private LoadingDialog loading;
	private ArrayList<String> lista=new ArrayList<>();
	private HashMap<String,String> list_share=new HashMap<>();
	private String base64="";
	private String update = "1.7.1";
	private String ruta_de_la_ultima_carpeta;
	
	private WindowManager wm;
	private boolean flotante=false;
	private View viewFlotante;
	private int FLAG_BASE64;
	private ArrayList<String> key_partes = new ArrayList<>();
	private ListCopyAdapter listcopyadater;
	
	private String nombre="B64";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		shared=PreferenceManager.getDefaultSharedPreferences(this);
		
		button_pegar=findViewById(R.id.button_pegar);
		button_save=findViewById(R.id.button_save);
		button_preference=findViewById(R.id.button_preferences);
		button_search=findViewById(R.id.button_search);
		button_borrar=findViewById(R.id.button_borrar);
		textview=findViewById(R.id.mainTextView);
		listview=findViewById(R.id.mainListView);
		button_txt=findViewById(R.id.button_share);
		button_optimization=findViewById(R.id.button_optimization);
		
		listcopyadater= new ListCopyAdapter(lista);
		listview.setAdapter(listcopyadater);
		
		FLAG_BASE64=getBase64_flag();
		ruta_de_la_ultima_carpeta= shared.getString("ruta_de_la_ultima_carpeta",Environment.getExternalStorageDirectory().getAbsolutePath());
		if(!new File(ruta_de_la_ultima_carpeta).exists()){
			ruta_de_la_ultima_carpeta=Environment.getExternalStorageDirectory().getAbsolutePath();
			shared.edit().putString("ruta_de_la_ultima_carpeta",ruta_de_la_ultima_carpeta).commit();
		}
		
		if(shared.getString("directorio","").equals("")){
			shared.edit().putString("directorio",Environment.getExternalStorageDirectory().getAbsolutePath()+"/B64").commit();
		}
		
		folder = new File(shared.getString("directorio",Environment.getExternalStorageDirectory().getAbsolutePath()+"/B64"));
		
		
		
		loading=new LoadingDialog(this);
		//android.R.drawable.ic_menu_search
		if(!folder.exists()){
			folder.mkdir();
		}
		
		//int nn = android.R.drawable.stat_sys_download;
		button_borrar.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					lista.clear();
					base64="";
					updateListView();
				}
			});
		button_search.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if(shared.getString("filePicker","1").equals("1")){
						showFilePicker(ruta_de_la_ultima_carpeta);
						return;
						}
					Intent i = new Intent(Intent.ACTION_GET_CONTENT);
					i.setType("*/*");
					startActivityForResult(i,2020);
					
				}
			});
			
		button_preference.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					startActivity(new Intent(MainActivity.this,SettingActivity.class));
					
				}
			});
			
		button_pegar.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					
					if(shared.getBoolean("tip1",true)){
						showMessage("Consejo\n\nPuede pulsar prolongadamente sobre éste botón para lanzar un flotante que le facilite pegar los mensajes.");
					}
					final String text = getTextForClipboard();
					if(!text.equals("")){
					if(lista.contains(text)){
					//showMessage("¡Error!\n\nNo se agregó el texto debido a que el contenido del portapapeles es igual al del texto");
					if(shared.getBoolean("igual",true)){
					AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
					d.setTitle("Pegar");
					d.setMessage("Ya existe un elemento en la lista similar al contenido del portapapeles.\n\n¿Seguro que quieres añadirlo?");
					d.setPositiveButton("si", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									addText(text);
								}
							});
							
						d.setNegativeButton("no", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									
								}
							});
							d.create().show();
					}else{
					addText(text);
					}
					}else{
					addText(text);
					}
					}else{
						toast("El portapapeles esta vacío");
					}
					
				}
			});
			
		button_pegar.setOnLongClickListener(new View.OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					if(shared.getBoolean("tip1",true)){
						shared.edit().putBoolean("tip1",false).commit();
					}
					mostrarFlotante();
					return true;
				}
			});
			
		button_save.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					
					if(lista.size()==0){
						toast("No hay contenido para guardar");
						return;
					}
					guardarDialog();
				}
			});
		
		button_txt.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if(lista.size()==0){
					toast("No hay contenido para guardar");
					}else{
					createTXT();
					}
				}
			});
			
		button_txt.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					File txtfile =new File(folder.getAbsolutePath()+"/B64TXT");
					if(txtfile.exists()){
						if(txtfile.listFiles().length>0){
						showFilePicker(txtfile.getAbsolutePath());
						}else{
							toast("La carpeta B64TXT esta vacía");
						}
					}else{
						toast("La capeta B64TXT no existe");
					}
					return true;
				}
			});
			
			
			
		button_optimization.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if(base64.equals("")){
						toast("No hay texto");
						return;
					}
					if(base64.contains("@")){
						toast("El texto ya fue optimizado");
						return;
					}
					new Thread(new Runnable(){
							@Override
							public void run()
							{
								
								showLoading();
								final int msize = base64.length();
								base64=compress(base64);
								hideLoading();
								handler.post(new Runnable(){

										@Override
										public void run()
										{
											createListCopy();
											updateTextView();
											showMessage("Se ahorraron "+getFileSize(MainActivity.this,msize-base64.length()));
											button_optimization.setVisibility(View.GONE);
										}
									});
							}
						}).start();
				}
			});
			
			
			
		if(!shared.getString("update","").equals(update)){
			showMessage(getTextAsset("update.txt"));
			shared.edit().putString("update",update).commit();
		}
			if(!shared.getBoolean("info",false)){
					showMessage(getTextAsset("info.txt"));
					shared.edit().putBoolean("info",true).commit();
			}
		if(shared.getString("size","").equals("")){
			shared.edit().putString("size","256000").commit();
		}
		
		if(shared.getString("editor","").equals("")){
			shared.edit().putString("editor","20000").commit();
		}
		
		if(shared.getBoolean("igual",true)){
			shared.edit().putBoolean("igual",true).commit();
		}
		if(!shared.getString("guardar","").equals("")){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Restaurar");
			dialog.setMessage("Hay una copia de seguridad en memoria que guardaste anteriormente.\n\n¿Quieres restaurarla ahora?");
			dialog.setPositiveButton("si", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						base64=shared.getString("guardar","");
						createListCopy();
					}
				});
				
			dialog.setNeutralButton("borrar", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						shared.edit().remove("guardar").commit();
					}
				});
				
			dialog.setNegativeButton("mas tarde", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
					}
				});
				
				dialog.create().show();
		}
		
		
		textview.setText("Almacenamiento disponible: "+getFileSize(this,folder.getFreeSpace()));
		
		textview.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					if(!base64.equals("")){
					sendListShare();
					}
				}
			});
		
		//showNotification();
		
		if(getIntent().getAction()==Intent.ACTION_SEND){
			Uri uri =(Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
			getBase64ForUri(uri);
		}
    }
	
	private void getBase64ForUri(Uri uri){
		if(uri!=null){
			String path = uri.getPath();
			if(new File(path).exists()){
				getBase64(path);
			}else{
				path = FileUtil.convertUriToFilePath(this,uri);
				if(new File(path).exists()){
					getBase64(path);
				}else{
					showMessage("No se encontró tu archivo");
				}
			}
		}
	}
	
	private String getTextAsset(String name){
		try
		{
			InputStream in = getAssets().open(name);
			byte[]b=new byte[in.available()];
			in.read(b);
			in.close();
			return new String(b,"UTF-8");
		}
		catch (Exception e)
		{return e.toString();}
		
	}
	
	private void updateListView(){
		if(true){
			listcopyadater=new ListCopyAdapter(lista);
			listview.setAdapter(listcopyadater);
			return;
		}
		if(listcopyadater!=null){
				listcopyadater.notifyDataSetChanged();
		}
	}
	
	
	
	public void addText(String text){
		
		if(text.startsWith("ID ")){
			int i = text.indexOf("\n");
			String key = text.substring(0,i);
			if(!key_partes.contains(key)){
			lista.add(text);
			updateListView();
			text = text.substring(i+1,text.length());
			base64=base64.concat(text.trim());
			updateTextView();
			toast( key + " agregada");
			key_partes.add(key);
			}else{
				toast("❌ No se agrego la "+key+ " debido a que ya la habías agregado");
			}
		}else{
		lista.add(text);
		base64=base64.concat(text);
		updateListView();
		updateTextView();
		toast("Texto agregado, "+lista.size()+" elemento(s) en la lista");
		}
	}
	
	
	private void sendListShare(){
		Iterator it = list_share.values().iterator();
		String str = "";
		while(it.hasNext()){
			String s = (String)it.next();
			str = str.concat(s+"\n\n");
			//sendText((String)it.next());
		}
		
		Intent in = new Intent(this,HackActivity.class);
		in.putExtra("texto",str.trim());
		startActivity(in);
	}

	@Override
	protected void onDestroy()
	{
		cerrarFlotante();
		super.onDestroy();
	}
	
	private int getBase64_flag(){
		String flag = shared.getString("b64type","1");
		switch(flag){
			case "1":return android.util.Base64.DEFAULT;
			case "2":return android.util.Base64.NO_WRAP;
			case "3":return android.util.Base64.URL_SAFE;
			case "4":return android.util.Base64.NO_CLOSE;
			case "5":return android.util.Base64.NO_PADDING;
			case "6":return android.util.Base64.CRLF;
			default: return android.util.Base64.DEFAULT;
		}
	}
	
	void test(){
		File todus = new File("/data/data/cu.todus.android/databases/toDus.db");
		if(todus.exists()){
			showMessage("Base de datos de Todus detectada");
			File midb =new File(folder.getAbsolutePath()+"/toDus.db");
			try
			{
				FileInputStream in = new FileInputStream(todus);
				FileOutputStream out = new FileOutputStream(midb);

				byte buff[]=new byte[1024];
				int length;
				while((length=in.read(buff))>0){
					out.write(buff,0,length);
				}

				in.close();
				out.close();
				if(midb.exists()){
					showMessage("Base de datos de toDus clonada");
				}
			}
			catch (Exception e)
			{showMessage("Error\n\n"+e.toString());}
		}
	}
	
	private void showFilePicker(String _path){
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		FilePicker picker = new FilePicker(this,_path);
		picker.setOnPickFileSelected(new FilePicker.OnPickFileSelected(){

				@Override
				public void onPickFileSelected(String path_file_selected)
				{
					dialog.hide();
					getBase64(path_file_selected);
				}
			});
		dialog.setContentView(picker);
		dialog.show();
	}
	
	private void showLoading(){
		handler.post(new Runnable(){

				@Override
				public void run()
				{
					loading.show();
				}
			});
	}
	private void hideLoading(){
		handler.post(new Runnable(){

				@Override
				public void run()
				{
					loading.hide();
				}
			});
	}
	
	//CREADOR DE LA LISTA
	private void createListCopy(int part_size){
		int size = base64.length();
		int num = 0;
		int index = 0;
		String res = "";
		
		while(true){
			String parte= "ID "+(++num)+"\n";
			int p_size = parte.length();
			
			int i = index+part_size-p_size;
			if(i>=size){
				i=size;
			}
			res = res.concat(parte);
			res = res.concat(base64.substring(index,i));
			index = i;
			
			if(i==(size)){
				break;
			}
			
			}
		
		createListCopy(res);

	}
	
	private void createListCopy(String copy){
		lista.clear();
		int max = Integer.parseInt(shared.getString("size","256000"));
		int size = copy.length();
		int index = 0;
		int end = 0;
		while(true){
			end=index+max;
			if(end>=size){
				end=size;
			}
			lista.add(copy.substring(index,end));
			if(end==size){
				updateListView();
				break;
			}else{
				index=end;
			}
		}
	}
	
	private void createListCopy(){
		createListCopy(base64);
	}
	
	
	private void writeFile(final String name){
		final File file = new File(folder.getAbsolutePath()+"/"+name);
		if(file.exists()){
			showMessage("¡Error!\n\nEl archivo "+name+" ya existe en la ruta:\n\n"+file.getAbsolutePath());
		}else{
		new Thread(new Runnable(){

			@Override
			public void run()
			{
			showLoading();
			if(base64.contains(CharBuilder.SEPARATOR)){
				//toast("Restaurando");
				base64=descompress(base64);
			}
			writeFileForBase64(file.getAbsolutePath(),base64);
			//writeFileForB64(file.getAbsolutePath());
			hideLoading();
			if(file.exists()){
			showMessage("¡Exito!\n\nEl archivo "+file.getName()+" se guardó en: \n\n"+file.getAbsolutePath());
			}
			}
		}).start();
		
		}
	}
	
	private void writeFileForB64(String path){
		
		try
		{
			FileOutputStream out = new FileOutputStream(path);
			for(String b64 : lista){
				byte buff[]=android.util.Base64.decode(b64,android.util.Base64.DEFAULT);
				out.write(buff);
			}
			out.close();
		}
		catch (Exception e)
		{}

	}
	
	public void toast(String mns){
		float r1=getDip(10);
		int r2=(int)r1;
		android.graphics.drawable.GradientDrawable aaa = new android.graphics.drawable.GradientDrawable();
		aaa.setColor(0xFF121E24);
		aaa.setCornerRadius(r1);
		TextView tex = new TextView(getApplicationContext());
		tex.setText(mns);
		tex.setPadding(r2,r2,r2,r2);
		tex.setBackgroundDrawable(aaa);
		tex.setTextColor(0xFFFFFFFF);
		tex.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);



		Toast mensaje = Toast.makeText(getApplicationContext(),"Toast simple",Toast.LENGTH_LONG);
		mensaje.setGravity(Gravity.CENTER,0,0);
		mensaje.setView(tex);
		mensaje.show();
	}
	
	public void anim(View view){
		ObjectAnimator a = new ObjectAnimator();
		a.setTarget(view);
		a.setPropertyName("alpha");
		a.setFloatValues(0.25f,1);
		a.setInterpolator(new DecelerateInterpolator());
		a.setDuration(1000);
		a.start();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK){
			switch(requestCode){
				case 2020:
					getBase64ForUri(data.getData());
					return;
			}
		}
	}
	
	
	public void getBase64(String path){
		final File file = new File(path);
		if(file.exists()){
			if(file.getName().endsWith(".b64.txt")){
				new Thread(new Runnable(){
						@Override
						public void run()
						{
							showLoading();
							//sb = new StringBuffer(getTextForFile(file));
							//getBase64(file);
							base64=getTextForFile(file);
							handler.post(new Runnable(){

									@Override
									public void run()
									{
										//copiarEnLaLista();
										createListCopy();
										updateTextView();
										toast("B64TXT Obtenido");
										
									}
								});
							
							hideLoading();
							
						}
					}).start();
					
					return;
			}
			ruta_de_la_ultima_carpeta=file.getParentFile().getAbsolutePath();
			shared.edit().putString("ruta_de_la_ultima_carpeta",ruta_de_la_ultima_carpeta).commit();
			new Thread(new Runnable(){

					@Override
					public void run()
					{
						showLoading();
						base64=getBase64ForFile(file.getAbsolutePath());
						handler.post(new Runnable(){
								@Override
								public void run()
								{
									
									nombre = file.getName();
									showMessage("¡ Operación exitosa !"
									+"\n\nEl archivo:\n"
									+nombre
									+
									"\n\nSe se exportó a base64, el siguiente paso es compartir cada una de las partes del archivo."
									+"\n\nInfo:\n"
									+"Tamaño del archivo original: "+getFileSize(MainActivity.this,file.length())
									+"\nTamaño del texto generado: "+getFileSize(MainActivity.this,base64.length())
									+"\nTotal de caracteres: "+base64.length()
									);
									if(shared.getBoolean("partes",false)){
										createListCopy(Integer.parseInt(shared.getString("id_size","4000")));
									}else{
									//copiarEnLaLista();
									createListCopy();
									}
									updateTextView();
									hideLoading();
									}
							});
					}
				}).start();


		}else{
			showMessage("¡Error!\n\nNo se encontró el archivo");
		}
	}
	
	public void showMessage(final String mns){
		handler.post(new Runnable(){

				@Override
				public void run()
				{
					new AlertMessage(MainActivity.this).setMessage(mns).show();
				}
			}); 
	}
	
	
	
	
	
	
		public String getBase64ForFile(String path){
			File file = new File(path);
			if(!file.exists()){
				return "";
			}
			try
			{
				FileInputStream in = new FileInputStream(file);
				byte buff[]=new byte[in.available()];
				in.read(buff);
				in.close();

				return android.util.Base64.encodeToString(buff,FLAG_BASE64);

			}
			catch (Exception e)
			{
				return "";
			}
		}

		private void writeFileForBase64(String path_out,String base64){
			File file = new File(path_out);
			try{
			byte buff[]= android.util.Base64.decode(base64,FLAG_BASE64);
				FileOutputStream out = new FileOutputStream(file);
				out.write(buff);
				out.close();
			}
			catch (Exception e)
			{
			showMessage(e.toString());
			}

		}
	
		
		
		
	
	public static class App extends Application {


		private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

		@Override
		public void onCreate() {

			this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread thread, Throwable ex) {
						Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

						intent.putExtra("error", getStackTrace(ex));

						PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);


						AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
						am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);

						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(2);

						uncaughtExceptionHandler.uncaughtException(thread, ex);
					}
				});
			super.onCreate();

		}


		private String getStackTrace(Throwable th){
			final Writer result = new StringWriter();

			final PrintWriter printWriter = new PrintWriter(result);
			Throwable cause = th;

			while(cause != null){
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			final String stacktraceAsString = result.toString();
			printWriter.close();

			return stacktraceAsString;
		}
	}
	
	
	public static class DebugActivity extends Activity {

		String[] exceptionType = {
			"StringIndexOutOfBoundsException",
			"IndexOutOfBoundsException",
			"ArithmeticException",
			"NumberFormatException",
			"ActivityNotFoundException"

		};

		String[] errMessage= {
			"Invalid string operation\n",
			"Invalid list operation\n",
			"Invalid arithmetical operation\n",
			"Invalid toNumber block operation\n",
			"Invalid intent operation"
		};


		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);



			Intent intent = getIntent();
			String errMsg = "";
			String madeErrMsg = "";
			if(intent != null){
				errMsg = intent.getStringExtra("error");

				String[] spilt = errMsg.split("\n");
				//errMsg = spilt[0];
				try {
					for (int j = 0; j < exceptionType.length; j++) {
						if (spilt[0].contains(exceptionType[j])) {
							madeErrMsg = errMessage[j];

							int addIndex = spilt[0].indexOf(exceptionType[j]) + exceptionType[j].length();

							madeErrMsg += spilt[0].substring(addIndex, spilt[0].length());
							break;

						}
					}

					if(madeErrMsg.isEmpty()) madeErrMsg = errMsg;
				}catch(Exception e){}

			}

			new AlertMessage(this).setMessage(":( Error en la aplicación\n\n"+madeErrMsg).show();

		}
	}
	
	
	
	
	public static class FilePicker extends LinearLayout{

		public interface OnPickFileSelected{void onPickFileSelected(String path_file_selected);}
		private OnPickFileSelected listener;
		ArrayList<String> files = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> info = new ArrayList<>();
		ArrayList<Integer> icon = new ArrayList<>();
		Button atras;
		TextView titulo;
		String f_path="";
		String _path;


		public FilePicker(Context context,String _path){
			super(context);
			this._path=_path;
			init();
		}

		public void setOnPickFileSelected(OnPickFileSelected onpickfileselected){
			listener=onpickfileselected;
		}

		public void init(){
			
			setOrientation(LinearLayout.VERTICAL);
			final ListView listview = (ListView)View.inflate(getContext(),R.layout.xlistview,null);
			atras=new Button(getContext());
			titulo=new TextView(getContext());
			titulo.setTextColor(0xffffffff);
			titulo.setPadding(5,5,5,5);
			LinearLayout bar = new LinearLayout(getContext());
			bar.setBackgroundColor(0xff0096f3);
			bar.setGravity(Gravity.CENTER_VERTICAL);
			bar.setPadding(5,5,5,5);
			bar.addView(atras);
			bar.addView(titulo);
			atras.setBackgroundColor(Color.WHITE);
			addView(bar);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
					{
						File f = new File(files.get(p3));
						if(f.isDirectory()){
							try{
							openFolder(listview,f.getAbsolutePath());
							}catch(Exception e){
								Toast.makeText(getContext(), e.toString(),Toast.LENGTH_SHORT).show();
							}
						}else{
							if(f.isFile()){
										if(listener!=null){
											
											listener.onPickFileSelected(f.getAbsolutePath());
											Toast.makeText(listview.getContext(),f.getAbsolutePath(),Toast.LENGTH_SHORT).show();
										
									
								}
							}
						}
					}
				});

			atras.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						File file = new File(f_path);
						if(file.getAbsolutePath().equals("/")){
							return;
						}
						
						File padre = file.getParentFile();
						if(padre.exists()){
							try{
							openFolder(listview,padre.getAbsolutePath());
							}catch(Exception e){
								Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
			atras.setText("⬅");
			atras.setTextColor(0xff160ea5);
			addView(listview);
			openFolder(listview,_path);
			}

		public void openFolder(ListView listview,String path){
			names.clear();
			files.clear();
			icon.clear();
			info.clear();
			
			
			File r = new File(path);
			titulo.setText(r.getAbsolutePath());
			f_path=r.getAbsolutePath();
			File filearr[] = r.listFiles();
			
			ArrayList<File> carpetas = new ArrayList<>();
			ArrayList<File> archivos = new ArrayList<>();
			
			for(File f:filearr){
				if(f.isDirectory()){
					carpetas.add(f);
				}else if(f.isFile()){
					archivos.add(f);
				}
			}
			
			ArrayList<File> todos = new ArrayList<>();
			todos.addAll(carpetas);
			todos.addAll(archivos);
			
			for(File file1:todos){
				if(file1.isDirectory()){
					files.add(file1.getAbsolutePath());
					names.add(file1.getName());
					icon.add(R.drawable.ic_folder);
					info.add(getFecha(file1.lastModified()));
				}else if(file1.isFile()){
					names.add(file1.getName());
					files.add(file1.getAbsolutePath());
					icon.add(R.drawable.ic_file);
					info.add(getFileSize(getContext(),file1)+"\n"+getFecha(file1.lastModified()));
				}
			}

			listview.setAdapter(new ListAdapter(names,files,getContext(),icon,info));
		}

	}
	
	//[LIST FILES]
	public static class ListAdapter extends BaseAdapter
	{
		ArrayList<String> names;
		ArrayList<String> rutas;
		ArrayList<String> info;
		ArrayList<Integer> icon;
		Context context;
		public ListAdapter(ArrayList<String> names,ArrayList<String> rutas,Context context,ArrayList<Integer> icon,ArrayList<String> info ){
			this.names=names;
			this.rutas=rutas;
			this.icon=icon;
			this.info=info;
			this.context=context;
		}

		@Override
		public int getCount()
		{
			return names.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return rutas.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			return 0;
		}

		@Override
		public View getView(int i, View p2, ViewGroup p3)
		{
			View v = View.inflate(context,R.layout.list_adapter,null);
			TextView text1=v.findViewById(R.id.list_adapterTextView1);
			TextView text2=v.findViewById(R.id.list_adapterTextView2);
			TextView text3 = v.findViewById(R.id.list_adapterTextView3);
			ImageView icono = v.findViewById(R.id.list_adapterImageView);
			icono.setImageResource(icon.get(i));
			text1.setText(names.get(i));
			text2.setText(rutas.get(i));
			text3.setText(info.get(i));
			return v;
		}


	}
	
	 // *********    //
	//  *********	//
   //   // ***	   //
  //   //  ***	  //
 //   //   ***   //
	 //		*   //
	//[LIST COPIAR]
	
	private class ListCopyAdapter extends BaseAdapter
	{
		ArrayList<String> list;
		ArrayList<Float> scale = new ArrayList<>();
		
		Context context;
		public ListCopyAdapter(ArrayList<String> list){
			this.list=list;
			for(int i = 0;i<list.size();i++){
				scale.add((float)1);
			}
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return list.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
			return 0;
		}

		@Override
		public View getView(final int i, View p2, ViewGroup p3)
		{
			View v = View.inflate(MainActivity.this,R.layout.list_copiar,null);
			TextView t1=v.findViewById(R.id.list_copiarTextView1);
			TextView t2=v.findViewById(R.id.list_copiarTextView2);
			TextView t3=v.findViewById(R.id.list_copiarTextView3);
			ImageButton btn1=v.findViewById(R.id.list_copiarButton);
			ImageButton btn2=v.findViewById(R.id.list_copiarImageButton);
			ImageButton btn3=v.findViewById(R.id.list_copiarImageButton2);
			final LinearLayout lin = v.findViewById(R.id.list_copiarLinearLayout);
			String texto = list.get(i);
			
			t1.setText(""+(i+1));
			int size = texto.length();
			int size_editor = Integer.parseInt(shared.getString("editor","20000"));
			if(size>size_editor){
				btn3.setVisibility(View.GONE);
			}else{
				btn3.setOnClickListener(new View.OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							EditDialog(i);
						}
					});
			}
			t3.setText("size: "+size);
			if(size>100){
				t2.setText(texto.substring(0,100));
			}else{
				t2.setText(texto);
			}

			btn1.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						setTextToClipbord(list.get(i));
						toast("PATR "+(i+1)+" copiada al port");
					}
				});

			btn2.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						Intent in = new Intent(Intent.ACTION_SEND);
						in.setType("text/plain");
						in.putExtra(Intent.EXTRA_TEXT,list.get(i));
						in.putExtra(Intent.EXTRA_SUBJECT,"PART "+(i+1));
						startActivity(in);
					}
				});
				
			lin.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						
						if(lin.getScaleX()==1){
							lin.animate().scaleX(0.95f).scaleY(0.95f).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
							scale.set(i,(float)0.95);
							list_share.put(String.valueOf(i),list.get(i));
						}else{
							lin.animate().scaleX(1).scaleY(1).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
							scale.set(i,(float)1);
							list_share.remove(String.valueOf(i));
						}
						
						if(list_share.size()<2){
							animTextView(textview);
						}
						
						if(list_share.size()==0){
							updateTextView();
						}else{
							textview.setText("Ver "+list_share.size()+" elemento(s)");
						}
					}
				});
				
			lin.setScaleX((float)scale.get(i));
			lin.setScaleY((float)scale.get(i));
			return v;
		}
	}
	
	
	public static String getFecha(long fecha){
		return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a").format(new Date(fecha));
	}
	
	public static String getFileSize(Context context, File file){
		return android.text.format.Formatter.formatFileSize(context,file.length());
	}
	
	public static String getFileSize(Context context, long size){
		return android.text.format.Formatter.formatFileSize(context,size);
	}

	public void animTextView(View view){
		ObjectAnimator anim ;//= new ObjectAnimator();
		/*anim.setTarget(view);
		anim.setPropertyName("rotationY");
		anim.setFloatValues(-90,0);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(1000);
		anim.start();*/
		
		anim = new ObjectAnimator();
		anim.setTarget(view);
		anim.setPropertyName("TranslationX");
		anim.setFloatValues(-view.getWidth(),0);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(1000);
		anim.start();
	}
	
	
	
	
	
	public static class SettingActivity extends PreferenceActivity {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.settings);
			Preference telegram = findPreference("telegram");
			Preference todus = findPreference("todus");
			Preference ayuda = findPreference("ayuda");
			Preference whatsapp = findPreference("whatsapp");
			final EditTextPreference directorio = (EditTextPreference)findPreference("directorio");
			SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(this);
			
			directorio.setSummary(share.getString("directorio",""));
			
			directorio.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){

					@Override
					public boolean onPreferenceChange(Preference p1, Object p2)
					{
						File file = new File(directorio.getEditText().getText().toString());
						if(file.exists()){
						p1.setSummary(file.getAbsolutePath());
						mns("Directorio de tabajo cambiado a\n\n"+file.getAbsolutePath());
						return true;
						}else{
						
						mns("Error, no existe ninguna carpeta en esa ruta\n\n"+file.getAbsolutePath());
						return false;
						}
					}
				});
			
			telegram.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
					@Override
					public boolean onPreferenceClick(Preference p1)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://t.me/lazaroysr96"));
						startActivity(i);
						return false;
					}
				});
			ayuda.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
					@Override
					public boolean onPreferenceClick(Preference p1)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("mailto:lazaroyunier96@nauta.cu"));
						i.putExtra(Intent.EXTRA_SUBJECT,"Hola, soy usuario(a) de B64");
						startActivity(i);
						return false;
					}
				});
			todus.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
					@Override
					public boolean onPreferenceClick(Preference p1)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://chat.todus.cu/7321bd46e961541aa1869c9184b69eb7"));
						startActivity(i);
						return false;
					}
				});
				
			
			whatsapp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
					@Override
					public boolean onPreferenceClick(Preference p1)
					{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://chat.whatsapp.com/CHuByZKqP68LSJKkapiqY7"));
						startActivity(i);
						return false;
					}
				});
			
		}
		
		private void mns(String mns){
			new AlertMessage(this).setMessage(mns).show();
		}
	}
	
	public static class LoadingDialog extends Dialog{
		//private Context context;
		public LoadingDialog(Context context){
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			setCancelable(false);
			float basesize = context.getResources().getDisplayMetrics().density;
			int p =(int)( basesize*25);
			GradientDrawable gd = new GradientDrawable();
			gd.setColor(0xff121e24);
			gd.setCornerRadius(context.getResources().getDisplayMetrics().density*10);
			LinearLayout l = new LinearLayout(context);
			l.setPadding(p,p,p,p);
			ProgressBar prog = new ProgressBar(context);
			l.setGravity(Gravity.CENTER);
			l.setBackground(gd);
			l.addView(prog);
			setContentView(l);
		}
	}

	
	public void cerrarFlotante(){
		if(flotante){
			if(wm!=null){
				if(viewFlotante!=null){
					wm.removeView(viewFlotante);
					flotante=false;
				}
			}
		}
	}
	
	@Override
	public void onBackPressed()
	{
		if(base64.equals("")){
			cerrarFlotante();
			super.onBackPressed();
			return;
		}
		AlertDialog.Builder a = new AlertDialog.Builder(this);
		a.setTitle("Salir");
		a.setMessage("Pulse el botón guardar para crear una copia de los datos que hay actualmente en la app antes de salir");
		a.setPositiveButton("salir", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					cerrarFlotante();
					finish();
				}
			});
		a.setNeutralButton("Guardar", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					cerrarFlotante();
					if(shared.edit().putString("guardar",base64).commit()){
						toast("Guardado");
						finish();
					}
				}
			});
		a.setNegativeButton("cancelar", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
				}
			});
			a.create().show();
	}
	
	public void guardarDialog(){
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_salve);
		final LinearLayout linear = dialog.findViewById(R.id.dialog_salveLinearLayout);
		final TextView titulo = dialog.findViewById(R.id.dialog_salveTitulo);
		final TextView mensaje = dialog.findViewById(R.id.dialog_salveMensaje);
		final EditText edittext = dialog.findViewById(R.id.dialog_salveEditText);
		final Button button1 = dialog.findViewById(R.id.dialog_salveButton1);
		final Button button2 = dialog.findViewById(R.id.dialog_salveButton2);
		final String basepath = folder.getAbsolutePath();

		button1.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					dialog.dismiss();
				}
			});

		button2.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String name = edittext.getText().toString();
					if(!name.equals("")){
						dialog.dismiss();
						writeFile(name);
					}
				}
			});

		GradientDrawable gd1=new GradientDrawable();
		gd1.setColors(new int[]{0xffff0020,0xffff0060});
		gd1.setCornerRadius(10);
		button1.setBackground(gd1);

		GradientDrawable gd2=new GradientDrawable();
		gd2.setColors(new int[]{0xff00c020,0xff00c060});
		gd2.setCornerRadius(10);
		button2.setBackground(gd2);

		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.WHITE);
		gd.setCornerRadius(10);
		linear.setBackground(gd);

		gd = new GradientDrawable();
		gd.setColor(Color.WHITE);
		gd.setStroke(1,0xff00c0ff);
		edittext.setBackground(gd);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		titulo.setText("Guardar como");
		mensaje.setText("Indique el nombre del archivo y su extensión, este se guardará en:\n\n"+basepath);
		dialog.show();
	}
	
	
	
	
	public void EditDialog(final int index){
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_edit);
		final LinearLayout linear = dialog.findViewById(R.id.dialog_editLinearLayout1);
		final TextView titulo = dialog.findViewById(R.id.dialog_editTextView1);
		final EditText edittext = dialog.findViewById(R.id.dialog_editEditText1);
		final Button button1 = dialog.findViewById(R.id.dialog_editButton1);
		final Button button2 = dialog.findViewById(R.id.dialog_editButton2);
		

		button1.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					dialog.dismiss();
				}
			});

		button2.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String name = edittext.getText().toString();
					if(!name.equals("")){
						lista.set(index,name);
						
					}else{
						lista.remove(index);
					}
					copiListInBuilder();
					dialog.dismiss();
				}
			});
		int r = (int)getDip(10);
		GradientDrawable gd1=new GradientDrawable();
		gd1.setColors(new int[]{0xffff0020,0xffff0060});
		gd1.setCornerRadius(r);
		button1.setBackground(gd1);

		GradientDrawable gd2=new GradientDrawable();
		gd2.setColors(new int[]{0xff00c020,0xff00c060});
		gd2.setCornerRadius(r);
		button2.setBackground(gd2);

		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.WHITE);
		gd.setCornerRadius(r);
		linear.setBackground(gd);

		gd = new GradientDrawable();
		gd.setColor(Color.WHITE);
		gd.setStroke(1,0xff00c0ff);
		edittext.setBackground(gd);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		edittext.setText(lista.get(index));
		dialog.show();
	}
	
	public void copiListInBuilder(){
		//sb= new StringBuffer();
		base64="";
		for(String s :lista){
			base64=base64.concat(s);
			//sb.append(s);
		}
		updateListView();
		toast("Editado");
		updateTextView();
	}
	
	
	public void updateTextView(){
		if(!(base64.equals("")||lista.size()==0)){
			textview.setText(""+lista.size()+" Elemento en la lista | size: "+base64.length()+" ("+getFileSize(this,base64.length())+")");
		}
	}
	
	public void createTXT(){
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_salve);
		final LinearLayout linear = dialog.findViewById(R.id.dialog_salveLinearLayout);
		final TextView titulo = dialog.findViewById(R.id.dialog_salveTitulo);
		final TextView mensaje = dialog.findViewById(R.id.dialog_salveMensaje);
		final EditText edittext = dialog.findViewById(R.id.dialog_salveEditText);
		final Button button1 = dialog.findViewById(R.id.dialog_salveButton1);
		final Button button2 = dialog.findViewById(R.id.dialog_salveButton2);
		
		final File folderTxt = new File(folder.getAbsolutePath()+"/B64TXT");
		final String basepath = folderTxt.getAbsolutePath();
		if(!folderTxt.exists()){
			folderTxt.mkdir();
		}
		
		final byte[] txt = base64.getBytes();
		button1.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					dialog.dismiss();
				}
			});

		button2.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String name = edittext.getText().toString();
					if(!name.equals("")){
						File fileTxt = new File(folderTxt.getAbsolutePath()+"/"+name+".b64.txt");
						
						try
						{
							FileOutputStream out = new FileOutputStream(fileTxt);
							out.write(txt);
							out.close();
							
							if(fileTxt.exists()){
								showMessage("Exito\n\nArchivo guardado como documento de texto plano en:\n\n"+fileTxt.getAbsolutePath()+"\n\nNota, el archivo se convirtió a base64 y se guardó como txt, el objetivo de ésta función es ocultar el archivo de los ojos curiosos, enviarlo por cualquier aplicación consumirá datos móviles.");
							}
						}
						catch (Exception e)
						{}
						dialog.dismiss();
					}
				}
			});
			
			int r = (int)getDip(10);
		GradientDrawable gd1=new GradientDrawable();
		gd1.setColors(new int[]{0xffff0020,0xffff0060});
		gd1.setCornerRadius(r);
		button1.setBackground(gd1);

		GradientDrawable gd2=new GradientDrawable();
		gd2.setColors(new int[]{0xff00c020,0xff00c060});
		gd2.setCornerRadius(r);
		button2.setBackground(gd2);

		GradientDrawable gd = new GradientDrawable();
		gd.setColor(Color.WHITE);
		gd.setCornerRadius(r);
		linear.setBackground(gd);

		gd = new GradientDrawable();
		gd.setColor(Color.WHITE);
		gd.setStroke(1,0xff00c0ff);
		edittext.setBackground(gd);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		titulo.setText("Guardar como txt");
		mensaje.setText("El archivo se guardará en la carpeta:\n\n"+basepath+"\n\nEsta opción te permite guardar el texto en base64 en un archivo txt, el objetivo de esto es que tengas un método para ocultar archivos de los ojos curiosos, enviar este txt por cualquier aplicación consumirá datos móviles.");
		dialog.show();
	}
	
	
public float getDip(int _input) {
return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input,getResources().getDisplayMetrics());
}

public String getTextForFile(File file){
	try
	{
		FileInputStream in = new FileInputStream(file);
		byte buff[] = new byte[in.available()];
		in.read(buff);
		return new String(buff);
	}
	catch (Exception e)
	{}
	
	return "";
}


public void mostrarFlotante() {
		if(flotante){
			return;
		}
		wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		final Display display = wm.getDefaultDisplay();
		
		layoutParams.gravity = Gravity.LEFT|Gravity.TOP;
		
		layoutParams.x=display.getWidth()-100;
		layoutParams.y=100;
		
		
		layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		layoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		layoutParams.format=PixelFormat.TRANSPARENT;
		layoutParams.alpha=1;
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.windowAnimations =android.R.style.Animation_Translucent;
		
		
		viewFlotante = View.inflate(this,R.layout.flotante, null);
		final ImageButton btn_cerrar = viewFlotante.findViewById(R.id.flotanteImageButtonCerrar);
		final LinearLayout l1=viewFlotante.findViewById(R.id.flotanteLinearLayout);
		final ImageButton btn_copy = viewFlotante.findViewById(R.id.flotanteImageButton);
		
	btn_copy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				if(!getTextForClipboard().equals("")){
				addText(getTextForClipboard());
				}else{
				toast("El portapapeles esta vacío");
				}
			}
		});
		
		
		btn_cerrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wm.removeView(viewFlotante);
				flotante=false;
				//startActivity(new Intent(MainActivity.this,MainActivity.class));
			}
		});
		
	l1.setOnTouchListener(new View.OnTouchListener(){
			private int x,y;

			@Override public boolean onTouch(View _v,MotionEvent evento){

				switch(evento.getAction()){
					case MotionEvent.ACTION_DOWN:
						x=(int)evento.getX();
						y=(int)evento.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						layoutParams.x=((int)evento.getRawX())-(_v.getWidth()/2);
						layoutParams.y=((int)evento.getRawY())-(_v.getHeight()/2);
						wm.updateViewLayout(viewFlotante,layoutParams);
						break;
				}
				return true;
			}});
		
			    
		wm.addView(viewFlotante,layoutParams);
		flotante=true;
	}
	
	
	public void getBase64(File file){
		
		try
		{
			FileInputStream in = new FileInputStream(file);
			byte buff[]=new byte[1024];
			int length;
			
			while((length=in.read(buff))>0){
				String parte = android.util.Base64.encodeToString(buff,0,length,android.util.Base64.DEFAULT);
				base64=base64.concat(parte);
				//sb.append(parte);
			}
			in.close();
			
		}
		catch (Exception e)
		{
			showMessage(e.toString());
		}

	}
	
public void encode(String _path,TextView _textview){
	File file = new File(_path);
	if(file.exists()){
		
		try
		{
			java.io.FileInputStream in = new java.io.FileInputStream(file);
			byte buff[]=new byte[in.available()];
			in.read(buff);
			in.close();
			
			base64 = android.util.Base64.encodeToString(buff,android.util.Base64.DEFAULT);
			
		}
		catch (Exception e)
		{}
	}
	
}
public void decode(String _path_out,String _base64){
	java.io.File file = new java.io.File(_path_out);
		
		try
		{
			java.io.FileOutputStream out = new java.io.FileOutputStream(file);
			
			byte buff[] = android.util.Base64.decode(_base64,android.util.Base64.DEFAULT);
			out.write(buff);
			out.close();
			
		}
		catch (Exception e)
		{}
	
	
}

private void setTextToClipbord(String texto){
	ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	ClipData clip = ClipData.newPlainText("text/plain",texto);
	clipboard.setPrimaryClip(clip);
}

private String getTextForClipboard(){
	ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	if(clipboard.getText()==null){
		if(clipboard.getPrimaryClip()!=null){
			ClipData clip = clipboard.getPrimaryClip();
			return clip.toString();
		}else{
			return "";
		}
	}else{
		return clipboard.getText().toString();
	}
}


private void sendText(String text, String name){
	Intent in = new Intent(Intent.ACTION_SEND);
	in.setType("text/plain");
	in.putExtra(Intent.EXTRA_TEXT,text);
	in.putExtra(Intent.EXTRA_SUBJECT,name);
	startActivity(in);
}
	private void sendText(String text){
		Intent in = new Intent(Intent.ACTION_SEND);
		in.setType("text/plain");
		in.putExtra(Intent.EXTRA_TEXT,text);
		in.putExtra(Intent.EXTRA_SUBJECT,"B64");
		startActivity(in);
	}
	
	public static class HackActivity extends Activity
	{
		private TextView texthack;
		@Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.hack);
			texthack=findViewById(R.id.hackTextView);
			
			texthack.setText(getIntent().getExtras().getString("texto"));
		}
		
	}
	
public static class FileUtil{
public static String convertUriToFilePath(final Context context, final Uri uri) {
String path = null;
if (DocumentsContract.isDocumentUri(context, uri)) {
if (isExternalStorageDocument(uri)) {
final String docId = DocumentsContract.getDocumentId(uri);
final String[] split = docId.split(":");
final String type = split[0];

if ("primary".equalsIgnoreCase(type)) {
path = Environment.getExternalStorageDirectory() + "/" + split[1];
}
} else if (isDownloadsDocument(uri)) {
final String id = DocumentsContract.getDocumentId(uri);

if (!android.text.TextUtils.isEmpty(id)) {
if (id.startsWith("raw:")) {
return id.replaceFirst("raw:", "");
}
}

final Uri contentUri = ContentUris
.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

path = getDataColumn(context, contentUri, null, null);
} else if (isMediaDocument(uri)) {
final String docId = DocumentsContract.getDocumentId(uri);
final String[] split = docId.split(":");
final String type = split[0];

Uri contentUri = null;
if ("image".equals(type)) {
contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
} else if ("video".equals(type)) {
contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
} else if ("audio".equals(type)) {
contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
}

final String selection = MediaStore.Audio.Media._ID + "=?";
final String[] selectionArgs = new String[]{
split[1]
};

path = getDataColumn(context, contentUri, selection, selectionArgs);
}
} else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
path = getDataColumn(context, uri, null, null);
} else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
path = uri.getPath();
}

if (path != null) {
try {
return java.net.URLDecoder.decode(path, "UTF-8");
}catch(Exception e){
return null;
}
}
return null;
}

private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
Cursor cursor = null;

final String column = MediaStore.Images.Media.DATA;
final String[] projection = {
column
};

try {
cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
if (cursor != null && cursor.moveToFirst()) {
final int column_index = cursor.getColumnIndexOrThrow(column);
return cursor.getString(column_index);
}
} catch (Exception e) {

} finally {
if (cursor != null) {
cursor.close();
}
}
return null;
}


private static boolean isExternalStorageDocument(Uri uri) {
return "com.android.externalstorage.documents".equals(uri.getAuthority());
}

private static boolean isDownloadsDocument(Uri uri) {
return "com.android.providers.downloads.documents".equals(uri.getAuthority());
}

private static boolean isMediaDocument(Uri uri) {
return "com.android.providers.media.documents".equals(uri.getAuthority());
}
}


public static class Simple
{
	public static String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	
	public static String decode(String data){
		for(int i = 0;i<base.length();i++){
			String x = ""+base.charAt(i);
			String x5=x+x+x+x+x;
			String x4=x+x+x+x;
			String x3=x+x+x;

			data=data.replace(x+"$",x5);
			data=data.replace(x+"#",x4);
			data=data.replace(x+"-",x3);

		}
		return data;
	}
	public static String encode(String data){
		
		for(int i = 0;i<base.length();i++){
			String x = ""+base.charAt(i);
			String x5=x+x+x+x+x;
			String x4=x+x+x+x;
			String x3=x+x+x;
			
			data=data.replace(x5,x+"$");
			data=data.replace(x4,x+"#");
			data=data.replace(x3,x+"-");
			
		}
		
		
		
		
		
		return data;
	}
}
public static String compress(String data)
	{
		StringBuilder sb = new StringBuilder();
		data=data.replace("\n","");
		CharBuilder cb =null;
		int count = 0;
		
		while(true){
			char c = data.charAt(count);
			
			if(cb==null){
				cb = new CharBuilder(c);
			}else{
				
				if(cb.igual(c)){
					++cb.count;
				}else{
					sb.append(cb.getEncode());
					cb=new CharBuilder(c);
				}
				
				
			}
			
			++count;
			if(count==data.length()){
				sb.append(cb.getEncode());
				return Simple.encode( sb.toString());
			}
		}

	}
	
	


	public static String descompress(String data){
		StringBuilder sb = new StringBuilder();
		data= Simple.decode(data);
		String arr[]= data.split(CharBuilder.DELIMITER);
		for(String s:arr){
			if(s.contains(CharBuilder.SEPARATOR)){
				String x []=s.split(CharBuilder.SEPARATOR);
				if(x.length==2){
					CharBuilder cc = new CharBuilder(x[0].charAt(0));
					cc.count=Integer.valueOf(x[1]);
					sb.append(cc.toString());
				}else{
					sb.append(s);
				}
			}else{
				sb.append(s);
			}
		}

		return sb.toString();
	}


	public static int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}

	public static class CharBuilder {
		char ch;
		int count = 1;
		public static String DELIMITER="!";
		public static String SEPARATOR="@";
		public CharBuilder(char ch){
			this.ch=ch;
		}

		public boolean igual(char ch){
			return this.ch==ch;
		}
		
		public String getEncode(){
			if(count<=5){
				return this.toString();
			}else{
				return DELIMITER+ch+SEPARATOR+count+DELIMITER;
			}
		}
		
		public String toString(){
			if(count==1){
				return String.valueOf(ch);
			}else{
				StringBuilder sb = new StringBuilder(ch);
				for(int i = 0;i<count;i++){
					sb.append(ch);
				}

				return sb.toString();
			}
		}
	}
		
}
