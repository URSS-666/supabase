/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BD;

import com.Vista.secciones.Menu;
import com.Vista.secciones.Pedido;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import BD.Database;
/**
 *
 * @author Sofia Useche
 */
public class CPedido {
    private JPanel panelSeleccionado;
    private Pedido PedidoInstance;

    public CPedido(Pedido PedidoInstance) {
        this.PedidoInstance = PedidoInstance;
    }

    public JPanel getPanelSeleccionado() {
        return panelSeleccionado;
    }
    
    public void mostrarMenu(Map<Integer, JPanel> panelesPorCategoria) {
        Database objetoConexion = new Database();
        String sql = "SELECT codigo, nombre, imagen, categoria_idcategorias FROM prueba160225.menu";

        try {
            Statement st = objetoConexion.establecerConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);

            for (JPanel panel : panelesPorCategoria.values()) {
                panel.removeAll();
                panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            }

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                int categoriaId = rs.getInt("categoria_idcategorias");
                byte[] imgBytes = rs.getBytes("imagen");

                ImageIcon icon = null;
                if (imgBytes != null) {
                    Image img = Toolkit.getDefaultToolkit().createImage(imgBytes);
                    Image resizedImg = img.getScaledInstance(143, 120, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(resizedImg);
                }

                JPanel itemPanel = new JPanel();
                itemPanel.setPreferredSize(new Dimension(173, 200));
                itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                itemPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
                itemPanel.setBackground(Color.WHITE);

                JLabel lblImagen = new JLabel(icon);
                lblImagen.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel lblNombre = new JLabel(nombre);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblNombre.setForeground(Color.BLACK);
                lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel lblCodigo = new JLabel(codigo);
                lblCodigo.setVisible(false);

                itemPanel.add(Box.createVerticalStrut(10));
                itemPanel.add(lblImagen);
                itemPanel.add(Box.createVerticalStrut(20));
                itemPanel.add(lblNombre);
                itemPanel.add(lblCodigo);
                itemPanel.add(Box.createVerticalGlue());

                itemPanel.putClientProperty("codigo", codigo);
                itemPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        seleccionarPanel(itemPanel);
                    }
                });

                JPanel panelCategoria = panelesPorCategoria.get(categoriaId);
                if (panelCategoria != null) {
                    panelCategoria.add(itemPanel);
                }
            }

            for (JPanel panel : panelesPorCategoria.values()) {
                panel.revalidate();
                panel.repaint();
            }

            rs.close();
            st.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar menú: " + e.toString());
        } finally {
            objetoConexion.cerrarConexion();
        }
    }
    
    public void seleccionarPanel(JPanel panel) {
        if (panelSeleccionado != null) {
            panelSeleccionado.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        }
        panelSeleccionado = panel;
        panelSeleccionado.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 3));
        String codigoSeleccionado = obtenerCodigoDelItem();
        if (codigoSeleccionado != null) {
            obtenerDatosCompletos(codigoSeleccionado); 
        }
    }
    public String obtenerCodigoDelItem() {
        if (panelSeleccionado == null) {
            return null;
        }
        return (String) panelSeleccionado.getClientProperty("codigo"); 
    }
    public void obtenerDatosCompletos(String codigo) {
        BD.Database objetoConexion = new BD.Database();
        String sql = "SELECT nombre, precio FROM prueba160225.menu WHERE codigo = CAST(? AS INTEGER)";

        try {
            PreparedStatement ps = objetoConexion.establecerConexion().prepareStatement(sql);
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                        PedidoInstance.mostrarDatoFormulario(codigo, nombre, precio);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener datos: " + e.toString());
        } finally {
            objetoConexion.cerrarConexion();
        }
    }

// DIEGO14.03.25:

public void enviarPedido(JTable jTable3) {
        Database db = new Database();
        Connection conexion = db.establecerConexion();
        
        if (conexion == null) {
            JOptionPane.showMessageDialog(null, "Error en la conexión a la base de datos");
            return;
        }
        
        try {
            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            
            String insertSQL = "INSERT INTO prueba160225.pedido (idpedido, cantidad, fechahora, precio, usuario_documento, mesa_idmesa, productomenú_idproducto_menú, observaciones, menu_nombre) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement ps = conexion.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    int idpedido = Integer.parseInt(model.getValueAt(i, 0).toString());
                    int cantidad = Integer.parseInt(model.getValueAt(i, 1).toString());
                    Date fechahora = java.sql.Date.valueOf(model.getValueAt(i, 2).toString());
                    double precio = Double.parseDouble(model.getValueAt(i, 3).toString());
                    int usuario_documento = Integer.parseInt(model.getValueAt(i, 4).toString());
                    int mesa_idmesa = Integer.parseInt(model.getValueAt(i, 5).toString());
                    int productomenu_id = Integer.parseInt(model.getValueAt(i, 6).toString());
                    String observaciones = model.getValueAt(i, 7) != null ? model.getValueAt(i, 7).toString() : null;
                    String menu_nombre = model.getValueAt(i, 8) != null ? model.getValueAt(i, 8).toString() : null;
                    
                    ps.setInt(1, idpedido);
                    ps.setInt(2, cantidad);
                    ps.setDate(3, fechahora);
                    ps.setDouble(4, precio);
                    ps.setInt(5, usuario_documento);
                    ps.setInt(6, mesa_idmesa);
                    ps.setInt(7, productomenu_id);
                    ps.setString(8, observaciones);
                    ps.setString(9, menu_nombre);
                    
                    ps.executeUpdate();
                    
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        model.setValueAt(id, i, 9); // Almacenar ID en columna oculta
                    }
                }
            }
            
            JOptionPane.showMessageDialog(null, "Pedido enviado correctamente");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al enviar pedido: " + e.toString());
        } finally {
            db.cerrarConexion();
        }
    }
// DIEGO14.03.25:
public void guardarPedido(JTable jTable3) {
    Database objetoConexion = new Database();
    
    try (Connection conexion = objetoConexion.establecerConexion()) {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        
        // Sentencia SQL para insertar pedidos en Supabase
        String insertSQL = "INSERT INTO pedido (cantidad, fechahora, precio, usuario_documento, mesa_idmesa, productomenú_idproducto_menú, observaciones, menu_nombre) VALUES (?, NOW(), ?, ?, ?, ?, ?, ?) RETURNING idpedido";

        try (PreparedStatement ps = conexion.prepareStatement(insertSQL)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                int cantidad = Integer.parseInt(model.getValueAt(i, 0).toString());
                double precio = Double.parseDouble(model.getValueAt(i, 1).toString());
                int usuarioDocumento = Integer.parseInt(model.getValueAt(i, 2).toString());
                int mesaId = Integer.parseInt(model.getValueAt(i, 3).toString());
                int productoId = Integer.parseInt(model.getValueAt(i, 4).toString());
                String observaciones = model.getValueAt(i, 5) != null ? model.getValueAt(i, 5).toString() : null;
                String menuNombre = model.getValueAt(i, 6) != null ? model.getValueAt(i, 6).toString() : null;

                ps.setInt(1, cantidad);
                ps.setDouble(2, precio);
                ps.setInt(3, usuarioDocumento);
                ps.setInt(4, mesaId);
                ps.setInt(5, productoId);
                ps.setString(6, observaciones);
                ps.setString(7, menuNombre);

                // Ejecutar la consulta y obtener el ID generado
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int idPedido = rs.getInt("idpedido");
                    model.setValueAt(idPedido, i, 7); // Guardar ID en la última columna
                }
            }
        }
        
        JOptionPane.showMessageDialog(null, "Pedido guardado correctamente en Supabase");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al guardar pedido en Supabase: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}


public void actualizarCantidadEnBD(String nombreProducto, int cantidad, int rowIndex, JTable jTable3) {
    Database objetoConexion = new Database();

    try {
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();

        // Obtener el ID del pedido desde la tabla
        Object idObj = model.getValueAt(rowIndex, 7); // Cambié la columna donde se almacena el ID
        if (idObj == null) {
            System.out.println("No se puede actualizar: la fila aún no está guardada en la base de datos");
            return;
        }

        int idPedido = Integer.parseInt(idObj.toString());
        Connection conn = objetoConexion.establecerConexion();

        // Obtener el precio del producto para calcular el nuevo total
        String selectSql = "SELECT precio FROM pedido WHERE idpedido = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, idPedido);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                double precio = rs.getDouble("precio");
                double nuevoTotal = cantidad * precio;

                // Actualizar la cantidad y el total
                String updateSql = "UPDATE pedido SET cantidad = ?, precio = ? WHERE idpedido = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, cantidad);
                    updateStmt.setDouble(2, nuevoTotal);
                    updateStmt.setInt(3, idPedido);
                    updateStmt.executeUpdate();
                }

                // Actualizar la tabla en la interfaz
                model.setValueAt(nuevoTotal, rowIndex, 3);
            }

            rs.close();
        }

        JOptionPane.showMessageDialog(null, "Cantidad actualizada correctamente en Supabase");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar cantidad en Supabase: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}

// DIEGO14.03.25:
public void actualizarCantidadEnBD(String nombre, int cantidad, int row, JTable table, boolean mostrarMensajes) {
    Database objetoConexion = new Database();

    try {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Obtener el ID del pedido desde la tabla (columna oculta)
        Object idObj = model.getValueAt(row, 7); // Ajustar al índice correcto en tu JTable
        if (idObj == null) {
            System.out.println("No se puede actualizar: la fila aún no está guardada en la base de datos");
            return;
        }

        int idPedido = Integer.parseInt(idObj.toString());
        Connection conn = objetoConexion.establecerConexion();

        // Obtener el precio del producto
        String selectSql = "SELECT precio FROM pedido WHERE idpedido = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, idPedido);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                double precio = rs.getDouble("precio");
                double nuevoTotal = cantidad * precio;

                // Actualizar la cantidad y el total en Supabase
                String updateSql = "UPDATE pedido SET cantidad = ?, precio = ? WHERE idpedido = ? RETURNING cantidad, precio";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, cantidad);
                    updateStmt.setDouble(2, nuevoTotal);
                    updateStmt.setInt(3, idPedido);
                    updateStmt.executeUpdate();
                }

                // Actualizar el modelo de la tabla en la interfaz
                model.setValueAt(nuevoTotal, row, 3);
            }

            rs.close();
        }

        // Mostrar mensaje si está habilitado
        if (mostrarMensajes) {
            JOptionPane.showMessageDialog(null, "Cantidad actualizada correctamente en Supabase");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar cantidad en Supabase: " + e.toString());
    } finally {
        objetoConexion.cerrarConexion();
    }
}

// DIEGO14.03.25:
private Connection obtenerConexion() throws Exception {
    Database objetoConexion = new Database();
    return objetoConexion.establecerConexion();
}

// Replace the example method with this implementation:
public void actualizarCantidadDirecta(String nombre, int cantidad) {
    try (Connection conn = obtenerConexion()) {

        // Buscar el producto en la tabla pedido
        String selectSql = "SELECT idpedido, precio FROM pedido WHERE menu_nombre = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, nombre);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int idPedido = rs.getInt("idpedido");
                double precio = rs.getDouble("precio");
                double nuevoTotal = cantidad * precio;

                // Actualizar la cantidad y el total en Supabase
                String updateSql = "UPDATE pedido SET cantidad = ?, precio = ? WHERE idpedido = ? RETURNING cantidad, precio";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, cantidad);
                    updateStmt.setDouble(2, nuevoTotal);
                    updateStmt.setInt(3, idPedido);
                    updateStmt.executeUpdate();
                }
            }
            rs.close();
        }

    } catch (Exception e) {
        System.out.println("Error en actualización directa en Supabase: " + e.getMessage());
    }
}
}
// DIEGO14.03.25:
