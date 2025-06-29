package com.softannate.apppuentedecomunicacion.ui.main.perfil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.softannate.apppuentedecomunicacion.R;
import com.softannate.apppuentedecomunicacion.databinding.FragmentPerfilBinding;
import com.softannate.apppuentedecomunicacion.modelos.Usuario;
import com.softannate.apppuentedecomunicacion.utils.DatePicker;
import com.softannate.apppuentedecomunicacion.utils.DateUtils;
import com.softannate.apppuentedecomunicacion.utils.Fecha;

import java.io.InputStream;

public class PerfilFragment extends Fragment {

    private EditText etNombre, etApellido, etDni, etTelefono, etFechaNacimiento, etDomicilio;
    private TextView tvEmail, tvRol, tvFoto;
    private ImageView fotoPerfil;
    private PerfilViewModel vmPerfil;
    private Button btnEditar, btnGuardar;
    private FragmentPerfilBinding bindingPerfil;
    private static int REQUEST_IMAGE_CAPTURE=1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       bindingPerfil= FragmentPerfilBinding.inflate(getLayoutInflater());
       vmPerfil =  new ViewModelProvider(this).get(PerfilViewModel.class);


        //inicializo campos
        etApellido = bindingPerfil.etApellido;
        etNombre = bindingPerfil.etNombre;
        etDni= bindingPerfil.etDni;
        etDomicilio= bindingPerfil.etDireccion;
        etTelefono= bindingPerfil.etTelefono;
        etFechaNacimiento=bindingPerfil.etFecha;
        tvEmail=bindingPerfil.tvEmail;
        tvRol=bindingPerfil.tvRol;
        btnGuardar= bindingPerfil.btGuardar;
        btnEditar= bindingPerfil.btEditar;
        tvFoto= bindingPerfil.tvFoto;
        fotoPerfil= bindingPerfil.ivFoto;

        btnGuardar.setVisibility(View.INVISIBLE);
        // Llamo a cambioEditText con 'false' para que los campos no sean editables
        boolean editable = false;
        vmPerfil.cambioEditText(bindingPerfil.navPerfil, editable);



     //   vmPerfil.obtenerUsuario();

        //observer p/Usuario
        vmPerfil.getMUsuario().observe(getViewLifecycleOwner(), new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                etNombre.setText(usuario.getNombre());
                etApellido.setText(usuario.getApellido());
                etDni.setText(String.valueOf(usuario.getDni()));
                etDomicilio.setText(usuario.getDomicilio());
                etTelefono.setText(String.valueOf(usuario.getTelefono()));
                etFechaNacimiento.setText(DateUtils.formatearFecha(String.valueOf(usuario.getFechaNacimiento())));
                Log.d("Fecha", DateUtils.formatearFecha(String.valueOf(usuario.getFechaNacimiento())));
                tvEmail.setText(String.valueOf(usuario.getEmail()));
                tvRol.setText(String.valueOf(usuario.getRol().getNombre()));
                Glide.with(getContext())
                        .load(usuario.getAvatar())
                        .placeholder(R.drawable.cargando)//imagen temporal mientras se carga
                        .diskCacheStrategy(DiskCacheStrategy.NONE)//desactivo la cache
                        .skipMemoryCache(true)
                        .error(R.drawable.perfil_user)
                        .into(fotoPerfil);
            }
        });

        etFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // DatePicker.showFechaInicioPicker(requireActivity(), etFechaNacimiento, null);


                // DatePicker.showDatePickerDialog(getActivity(), etFechaNacimiento, null,  null);
            }
        });




        //cambio boton editar
        bindingPerfil.btEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean editable = true;
                vmPerfil.cambioEditText(bindingPerfil.navPerfil, editable);
               // vmPerfil.obtenerUsuario();
                btnEditar.setVisibility(View.INVISIBLE);
                btnGuardar.setVisibility(View.VISIBLE);
                etFechaNacimiento.setFocusable(false);
            }
        });

        bindingPerfil.btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean editable = false;
                vmPerfil.cambioEditText(bindingPerfil.navPerfil, editable);

                // Obtengo usuario actual
                Usuario usuarioActual = vmPerfil.getMUsuario().getValue();

                // Capturo el email y seteo en el nuevo usuario
                String email = usuarioActual.getEmail();
                vmPerfil.setEmail(email);
                Usuario usuarioNuevo = new Usuario();

                // Seteo los demás campos
                usuarioNuevo.setEmail(email);
                usuarioNuevo.setDomicilio(etDomicilio.getText().toString());
                usuarioNuevo.setDni(etDni.getText().toString());
                usuarioNuevo.setApellido(etApellido.getText().toString());
                usuarioNuevo.setNombre(etNombre.getText().toString());
                usuarioNuevo.setDomicilio(etDomicilio.getText().toString());
                usuarioNuevo.setTelefono(etTelefono.getText().toString());
                // Uso el método de la clase Fecha para formatear la fecha
                String fechaFormateada = Fecha.formatearFechaParaApi(etFechaNacimiento.getText().toString());
                usuarioNuevo.setFechaNacimiento(fechaFormateada);
                usuarioNuevo.setRolId(usuarioActual.getRolId());
                Log.d("rol","ol"+usuarioNuevo.getRolId());

                // Envio el nuevo usuario al ViewModel para que lo procese
               // vmPerfil.editarPerfil(usuarioNuevo);

                // Cambio visibilidad de botones
                btnGuardar.setVisibility(View.INVISIBLE);
                btnEditar.setVisibility(View.VISIBLE);
            }
        });

        bindingPerfil.btPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_modificar_pass);
            }
        });

        bindingPerfil.tvFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cuadro de dialogo
                new AlertDialog.Builder(requireContext())
                        .setTitle("Seleccione una opción")
                        .setItems(new CharSequence[]{"Tomar una foto", "Elegir foto de la galeria", "Eliminar foto"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int opcion) {
                                switch (opcion){
                                    case 0:
                                        permisosDeCamara();
                                        break;
                                    case 1:
                                        cargarImagen();
                                        break;
                                    case 2:
                                        eliminarFoto();
                                        break;
                                }

                            }
                        })
                        .setNegativeButton("Cancelar",null)
                        .show();
            }
        });

        return  bindingPerfil.getRoot();
    }

    private void eliminarFoto() {
       // vmPerfil.eliminarAvatar();
        vmPerfil.getAvatar().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
    }

    public void permisosDeCamara(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},REQUEST_IMAGE_CAPTURE);
        }else{
            tomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto(); // Permiso otorgado, abre la cámara
            } else {
                Toast.makeText(getContext(), "Se requiere permiso para usar la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 20); // El requestCode 20 se usa para identificar la respuesta de la cámara
    }


    private Uri getImageUri(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    //para abrir a galeria
    public void cargarImagen(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //intent para seleccionar elemento de la lista(imagen)
        intent.setType("image/");//filtra para mostrar sólo imagenes
        Bundle numero = new Bundle();
        numero.putInt("id",10);//id de solicitud que se usa en onActivityResult
        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicacion..."),10);

    }

    //para cargar la imagen en el imageView
    private void auxImageUri(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);//abre flujo de entrada a partir de la URI de la imagen
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);//decodifica la imagen al formato Bitmap
            fotoPerfil.setImageBitmap(bitmap);//carga la imagen en el imageView
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null) {//verifica el id de la solicitud que sea el mismo que cuando inicio y que la activity se completo
            Uri path = data.getData();// Obtengo la URI de la imagen
            if (path != null) {
                auxImageUri(path);// Carga la imagen en el ImageView
              //  vmPerfil.subirAvatar(path, requireContext());//subo la imagen
            }
        } else if (requestCode == 20 && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(bitmap);
            auxImageUri(uri);
           // vmPerfil.subirAvatar(uri, requireContext());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bindingPerfil = null;
    }
  /*  private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // fecha máxima como la fecha actual
        long maxDate = calendar.getTimeInMillis();

        // DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
                    // Muestra la fecha seleccionada en el EditText
                    etFechaNacimiento.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                }, year, month, day);

        // Establezco la fecha máxima para evitar fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(maxDate);

        // Mostrar el DatePickerDialog
        datePickerDialog.show();
    }*/
}