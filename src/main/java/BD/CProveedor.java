/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BD;

import com.toedter.calendar.JDateChooser;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sofia Useche
 */
public class CProveedor {
    
    public void agregarProveedor(JTextField nombre, JTextField telefono, JTextField correo, JTextField direccion, JTextField tipoProveedor){
        Database objetoconexion = new Database();
        String consulta = "insert into prueba160225.proveedor (nombre,telefono,correo,direccion,tipoproveedor) values (?,?,?,?,?);";
        try {
            CallableStatement cs = objetoconexion.establecerConexion().prepareCall(consulta);
            cs.setString(1, nombre.getText().trim());
            cs.setInt(2, Integer.parseInt(telefono.getText().trim()));
            cs.setString(3, correo.getText().trim());
            cs.setString(4, direccion.getText().trim());
            cs.setString(5, tipoProveedor.getText().trim());
            cs.execute();
            JOptionPane.showMessageDialog(null, "Se guardo correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al guardar"+e.toString());
        }
    }
    
    public void mostrarProveedor(JTable tablaTotalProveedor){
        BD.Database objetoConexion = new BD.Database();
        DefaultTableModel modelo = new DefaultTableModel(){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) { 
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }            
        };
        String sql ="";
        modelo.addColumn("Seleccionar");
        modelo.addColumn("Codigo");
        modelo.addColumn("Nombre");
        modelo.addColumn("Telefono");
        modelo.addColumn("Correo");
        modelo.addColumn("Direccion");
        modelo.addColumn("Tipo de Producto");
        tablaTotalProveedor.setModel(modelo);
        sql = "select proveedor.idproveedor,proveedor.nombre,proveedor.telefono,proveedor.correo,proveedor.direccion,proveedor.tipoproveedor from prueba160225.proveedor ORDER BY proveedor.idproveedor ASC;";
        try {
            Statement st = objetoConexion.establecerConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {          
                String codigo = rs.getString("idproveedor");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                String correo = rs.getString("correo");
                String direccion = rs.getString("direccion");
                String tipoproveedor = rs.getString("tipoproveedor");
                modelo.addRow(new Object[]{false,codigo,nombre,telefono,correo,direccion,tipoproveedor});
                tablaTotalProveedor.setModel(modelo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error al mostrar proveedor"+e.toString());
        }
        finally{
            objetoConexion.cerrarConexion();
    }
    }
    public void Seleccionar(JTable totalProveedor, JTextField codigo, JTextField nombre, JTextField telefono, JTextField correo, JTextField direccion, JTextField tipoProveedor) {
        StringBuilder codigos = new StringBuilder();
        StringBuilder nombres = new StringBuilder();
        StringBuilder telefonos = new StringBuilder();
        StringBuilder correos = new StringBuilder();
        StringBuilder direcciones = new StringBuilder();
        StringBuilder tiposproveedores = new StringBuilder();

        for (int i = 0; i < totalProveedor.getRowCount(); i++) {
            Boolean seleccionado = (Boolean) totalProveedor.getValueAt(i, 0);
            if (seleccionado != null && seleccionado) { 
                codigos.append(totalProveedor.getValueAt(i, 1).toString()).append(", ");
                nombres.append(totalProveedor.getValueAt(i, 2).toString()).append(", ");
                telefonos.append(totalProveedor.getValueAt(i, 3).toString()).append(", ");
                correos.append(totalProveedor.getValueAt(i, 4).toString()).append(", ");
                direcciones.append(totalProveedor.getValueAt(i, 5).toString()).append(", ");
                tiposproveedores.append(totalProveedor.getValueAt(i, 6).toString()).append(", ");
            }
        }

        if (codigos.length() > 0) {
            codigo.setText(codigos.substring(0, codigos.length() - 2));
            nombre.setText(nombres.substring(0, nombres.length() - 2));
            telefono.setText(telefonos.substring(0, telefonos.length() - 2));
            correo.setText(correos.substring(0, correos.length() - 2));
            direccion.setText(direcciones.substring(0, direcciones.length() - 2));
            tipoProveedor.setText(tiposproveedores.substring(0, tiposproveedores.length() - 2));

        } else {
            JOptionPane.showMessageDialog(null, "No hay elementos seleccionados.");
        }
    }
    public void modificarProveedor(JTextField codigo,JTextField nombre, JTextField telefono, JTextField correo, JTextField direccion, JTextField tipoproveedor){
        Database objetoConexion = new Database();
        String consulta = "UPDATE prueba160225.proveedor SET nombre=?,telefono=?,correo=?,direccion=?,tipoproveedor=? WHERE idproveedor=?";
        try {
            CallableStatement cs = objetoConexion.establecerConexion().prepareCall(consulta);
            cs.setString(1, nombre.getText());
            cs.setInt(2, Integer.parseInt(telefono.getText()));
            cs.setString(3, correo.getText());
            cs.setString(4, direccion.getText());
            cs.setString(5, tipoproveedor.getText());
            cs.setInt(6, Integer.parseInt(codigo.getText()));
            cs.execute();
            JOptionPane.showMessageDialog(null, "Se modifico correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se modifico correctamente"+e.toString());
        }
        finally{
            objetoConexion.cerrarConexion();
        }
    }
    public void eliminarProveedor(JTable tablaProveedor) {
        Database objetoConexion = new Database();
        List<Integer> idsAEliminar = new ArrayList<>();

        for (int i = 0; i < tablaProveedor.getRowCount(); i++) {
            Object valor = tablaProveedor.getValueAt(i, 0);
            if (valor instanceof Boolean && (Boolean) valor) {
                try {
                    int idProveedor = Integer.parseInt(tablaProveedor.getValueAt(i, 1).toString());
                    idsAEliminar.add(idProveedor);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Error al obtener el ID del proveedor: " + e.getMessage());
                }
            }
        }

        if (idsAEliminar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un registro para eliminar.");
            return;
        }

        Object[] opciones = {"Sí", "No"};
        int confirmacion = JOptionPane.showOptionDialog(
            null,
            "¿Está seguro de eliminar " + idsAEliminar.size() + " registro(s)?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[1]
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String consulta = "DELETE FROM prueba160225.proveedor WHERE idproveedor = ?";

        try (Connection conexion = objetoConexion.establecerConexion();
             PreparedStatement ps = conexion.prepareStatement(consulta)) {

            conexion.setAutoCommit(false); 

            for (int id : idsAEliminar) {
                ps.setInt(1, id);
                ps.addBatch();
            }

            ps.executeBatch();
            conexion.commit();

            JOptionPane.showMessageDialog(null, "Se eliminó correctamente " + idsAEliminar.size() + " registro(s).");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudieron eliminar los registros: " + e.getMessage());
        }
    }
}
