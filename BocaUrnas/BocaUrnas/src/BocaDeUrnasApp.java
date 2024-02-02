import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BocaDeUrnasApp {

	private static List<Candidato> candidatos = new ArrayList<>();
    private static int idCandidato = 1;

    private static JPanel panelCursosAdmin;
    private static JPanel panelCursosUsuario;
    private static JPanel panelEstadisticas;
    private static JTextField txtNombreCandidato;
    private static JTextField txtApellidoCandidato;
    private static JTextField txtPartidoCandidato;
    private static JTextField txtNumeroLista;
    private static String pinAdmin = "1234";
    private static JDialog dialogoCandidatoAgregado;

    private static JTextField txtNombreVicepresidente;
    private static JTextField txtApellidoVicepresidente;
    private static JTextField txtNombreTesorero;
    private static JTextField txtApellidoTesorero;

    private static Map<String, Map<String, Integer>> votosPorParalelo = new HashMap<>();

    private static Map<String, Color> coloresCandidatos = new HashMap<>();
    private static Map<String, Integer> votosPorCandidato = new HashMap<>();

    private static List<String> paralelos = Arrays.asList("Paralelo A", "Paralelo B", "Paralelo C");
    private static List<String> cursosAdministrativos = Arrays.asList("Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4", "Mesa 5");
    private static List<String> cursosAcademicos = Arrays.asList("Octavo", "Noveno", "Décimo");

    private static JComboBox<String> comboParalelos;
    private static JComboBox<String> comboCursosAdministrativos;
    private static JComboBox<String> comboCursosAcademicos;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Boca de Urnas");

            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panelPrincipal = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JTabbedPane tabbedPane = new JTabbedPane();

            JPanel panelAdmin = crearPanelAdmin();
            JPanel panelUsuario = crearPanelUsuario();
            JPanel panelAdministrativa = crearPanelAdministrativa();

            tabbedPane.addTab("Admin", null, panelAdmin, "Panel de Administradores");
            tabbedPane.addTab("Administrativa", null, panelAdministrativa, "Panel Administrativo");

            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            panelPrincipal.add(tabbedPane, gbc);

            ventana.getContentPane().add(panelPrincipal);

            ventana.setSize(new Dimension(800, 600));
            ventana.setMinimumSize(new Dimension(400, 400));
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }

    private static JPanel crearPanelAdmin() {
        JPanel panelAdmin = new JPanel(new BorderLayout());

        JLabel labelTitulo = new JLabel("Agregar Candidatos");
        labelTitulo.setHorizontalAlignment(JLabel.CENTER);
        panelAdmin.add(labelTitulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelCursosAdmin = new JPanel();
        panelEstadisticas = new JPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre Presidente:"), gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtNombreCandidato = new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Apellido Presidente:"), gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtApellidoCandidato = new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelFormulario.add(new JLabel("Partido:"), gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtPartidoCandidato = new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelFormulario.add(new JLabel("Número de Lista:"), gbc);

        gbc.gridx = 1;
        panelFormulario.add(txtNumeroLista = new JTextField(15), gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        JButton btnAgregarCandidato = new JButton("Agregar Candidato");
        btnAgregarCandidato.addActionListener(e -> agregarCandidato());
        panelFormulario.add(btnAgregarCandidato, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        JButton btnVerListaCandidatos = new JButton("Ver Lista de Candidatos");
        btnVerListaCandidatos.addActionListener(e -> mostrarListaCandidatos());
        panelFormulario.add(btnVerListaCandidatos, gbc);

        panelAdmin.add(panelFormulario, BorderLayout.CENTER);
        panelAdmin.add(panelCursosAdmin, BorderLayout.SOUTH);
        panelAdmin.add(panelEstadisticas, BorderLayout.EAST);

        return panelAdmin;
    }
    
    private static JPanel crearPanelUsuario() {
        JPanel panelUsuario = new JPanel(new BorderLayout());

        JLabel labelTitulo = new JLabel("Votar por Candidato");
        labelTitulo.setHorizontalAlignment(JLabel.CENTER);
        panelUsuario.add(labelTitulo, BorderLayout.NORTH);

        JPanel panelVotacion = new JPanel();
        panelCursosUsuario = new JPanel();
        panelEstadisticas = new JPanel(new BorderLayout());

        JButton btnAccesoAdmin = new JButton("Acceder como Administrador");
        btnAccesoAdmin.addActionListener(e -> solicitarPIN());

        comboParalelos = new JComboBox<>(paralelos.toArray(new String[0]));

        panelUsuario.add(btnAccesoAdmin, BorderLayout.CENTER);
        panelUsuario.add(comboParalelos, BorderLayout.CENTER);
        panelUsuario.add(panelCursosUsuario, BorderLayout.SOUTH);
        panelUsuario.add(panelEstadisticas, BorderLayout.EAST);

        return panelUsuario;
    }

    private static JPanel crearPanelAdministrativa() {
        JPanel panelAdministrativa = new JPanel(new BorderLayout());

        JLabel labelTitulo = new JLabel("Registrar Mesas y Cursos");
        labelTitulo.setHorizontalAlignment(JLabel.CENTER);
        panelAdministrativa.add(labelTitulo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Seleccione Mesa:"), gbc);

        comboCursosAdministrativos = new JComboBox<>(cursosAdministrativos.toArray(new String[0]));
        gbc.gridx = 1;
        panelFormulario.add(comboCursosAdministrativos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelFormulario.add(new JLabel("Seleccione Curso:"), gbc);

        comboCursosAcademicos = new JComboBox<>(cursosAcademicos.toArray(new String[0]));
        gbc.gridx = 1;
        panelFormulario.add(comboCursosAcademicos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> registrarMesaYCurso());
        panelFormulario.add(btnRegistrar, gbc);

        panelAdministrativa.add(panelFormulario, BorderLayout.CENTER);

        return panelAdministrativa;
    }
    
    private static void registrarMesaYCurso() {
        String mesaSeleccionada = (String) comboCursosAdministrativos.getSelectedItem();
        String cursoSeleccionado = (String) comboCursosAcademicos.getSelectedItem();

        if (mesaSeleccionada != null && cursoSeleccionado != null) {
            JOptionPane.showMessageDialog(null, "Se ha registrado la mesa '" + mesaSeleccionada + "' para el curso de '" + cursoSeleccionado + "'.");
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una mesa y un curso.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void solicitarPIN() {
        String ingresoPIN = JOptionPane.showInputDialog(null, "Ingrese el PIN de administrador:", "Acceso de Administrador", JOptionPane.PLAIN_MESSAGE);
        if (ingresoPIN != null && ingresoPIN.equals(pinAdmin)) {
            JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, panelCursosUsuario);
            tabbedPane.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(null, "PIN incorrecto. Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void mostrarListaCandidatos() {
        StringBuilder listaCandidatos = new StringBuilder("Lista de Candidatos:\n");

        for (Candidato candidato : candidatos) {
            listaCandidatos.append(candidato.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, listaCandidatos.toString(), "Lista de Candidatos", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static void agregarCandidato() {
        String nombre = txtNombreCandidato.getText();
        String apellido = txtApellidoCandidato.getText();
        String partido = txtPartidoCandidato.getText();
        String numeroListaText = txtNumeroLista.getText();

        if (!nombre.isEmpty() && !apellido.isEmpty() && !partido.isEmpty() && !numeroListaText.isEmpty()) {
            try {
                int numeroLista = Integer.parseInt(numeroListaText);

                Candidato candidato = new Candidato(idCandidato++, nombre, apellido, partido, numeroLista);
                candidatos.add(candidato);
                coloresCandidatos.put(candidato.toString(), obtenerColorAleatorio());
                votosPorCandidato.put(candidato.toString(), 0);
                inicializarVotosPorParalelo(candidato.toString());
                agregarCardCandidatoUsuario(candidato);

                txtNombreCandidato.setText("");
                txtApellidoCandidato.setText("");
                txtPartidoCandidato.setText("");
                txtNumeroLista.setText("");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Número de Lista debe ser un valor numérico válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, ingresa todos los datos del candidato.");
        }
    }

    private static void mostrarEstudiantesSinVotar() {
        String paralelo = (String) comboParalelos.getSelectedItem();
        if (paralelo != null && !paralelo.isEmpty() && paralelos.contains(paralelo)) {
            List<String> estudiantesSinVotar = obtenerEstudiantesSinVotar(paralelo);

            if (!estudiantesSinVotar.isEmpty()) {
                StringBuilder mensaje = new StringBuilder("Estudiantes sin Votar en el Paralelo " + paralelo + ":\n");
                for (String estudiante : estudiantesSinVotar) {
                    mensaje.append(estudiante).append("\n");
                }
                JOptionPane.showMessageDialog(null, mensaje.toString(), "Estudiantes sin Votar", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Todos los estudiantes en el paralelo " + paralelo + " han votado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Paralelo incorrecto. Selecciona un paralelo válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static List<String> obtenerEstudiantesSinVotar(String paralelo) {
        Set<String> estudiantesVotaron = new HashSet<>();
        for (Map<String, Integer> votosPorEstudiante : votosPorParalelo.values()) {
            estudiantesVotaron.addAll(votosPorEstudiante.keySet());
        }

        List<String> estudiantesSinVotar = new ArrayList<>();
        List<String> estudiantesEnParalelo = obtenerEstudiantesPorParalelo(paralelo);

        for (String estudiante : estudiantesEnParalelo) {
            if (!estudiantesVotaron.contains(estudiante)) {
                estudiantesSinVotar.add(estudiante);
            }
        }

        return estudiantesSinVotar;
    }

    private static List<String> obtenerEstudiantesPorParalelo(String paralelo) {
        if ("Paralelo A".equals(paralelo)) {
            return Arrays.asList("Estudiante1", "Estudiante2", "Estudiante3");
        } else if ("Paralelo B".equals(paralelo)) {
            return Arrays.asList("Estudiante4", "Estudiante5", "Estudiante6");
        } else if ("Paralelo C".equals(paralelo)) {
            return Arrays.asList("Estudiante7", "Estudiante8", "Estudiante9");
        } else {
            return Collections.emptyList();  // Paralelo no reconocido
        }
    }

    private static void agregarBotonFinalizar1() {
        JButton btnFinalizar = new JButton("Finalizar");
        btnFinalizar.addActionListener(e -> {
            if (todosEstudiantesHanVotado()) {
                determinarGanador();
            } else {
                JOptionPane.showMessageDialog(null, "No se puede finalizar. No todos los estudiantes han votado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        panelEstadisticas.add(btnFinalizar, BorderLayout.SOUTH);

        panelEstadisticas.revalidate();
        panelEstadisticas.repaint();
    }

    private static boolean todosEstudiantesHanVotado() {
        Set<String> estudiantesVotaron = new HashSet<>();
        for (Map<String, Integer> votosPorEstudiante : votosPorParalelo.values()) {
            estudiantesVotaron.addAll(votosPorEstudiante.keySet());
        }

        List<String> todosEstudiantes = obtenerTodosEstudiantes();
        return estudiantesVotaron.containsAll(todosEstudiantes);
    }

    private static List<String> obtenerTodosEstudiantes() {
        return Arrays.asList("Estudiante1", "Estudiante2", "Estudiante3", "Estudiante4", "Estudiante5", "Estudiante6", "Estudiante7", "Estudiante8", "Estudiante9");
    }
    
    private static void agregarCardCandidatoUsuario(Candidato candidato) {
        JPanel cardCandidato = new JPanel();
        JButton btnVotar = new JButton("Votar por " + candidato.getNombre());
        btnVotar.addActionListener(e -> votarCandidato(candidato));
        cardCandidato.add(new JLabel(candidato.toString()));
        cardCandidato.add(btnVotar);
        panelCursosUsuario.add(cardCandidato);
        panelCursosUsuario.revalidate();
    }

    private static void inicializarVotosPorParalelo(String candidato) {
        for (String paralelo : paralelos) {
            votosPorParalelo.computeIfAbsent(candidato, k -> new HashMap<>()).put(paralelo, 0);
        }
    }

    private static void mostrarVotantes() {
    }

    private static void mostrarListaCandidatos1() {
        StringBuilder listaCandidatos = new StringBuilder("Lista de Candidatos:\n");

        for (Candidato candidato : candidatos) {
            listaCandidatos.append("ID: ").append(candidato.getId())
                           .append(", Nombre: ").append(candidato.getNombre())
                           .append(" ").append(candidato.getApellido())
                           .append(", Partido: ").append(candidato.getPartido())
                           .append(", Número de Lista: ").append(candidato.getNumeroLista())
                           .append("\n");
        }
        JOptionPane.showMessageDialog(null, listaCandidatos.toString(), "Lista de Candidatos", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void votarCandidato(Candidato candidato) {
        String paralelo = (String) comboParalelos.getSelectedItem();
        if (paralelo != null && !paralelo.isEmpty() && paralelos.contains(paralelo)) {
            votosPorCandidato.put(candidato.toString(), votosPorCandidato.get(candidato.toString()) + 1);
            votarCandidatoPorParalelo(candidato.toString(), paralelo);
            JOptionPane.showMessageDialog(null, "Votaste por: " + candidato.toString() + " en el paralelo " + paralelo);
            actualizarEstadisticasAdmin();
            actualizarEstadisticasPorParalelo();
            mostrarLider();
            agregarBotonFinalizar1();
        } else {
            JOptionPane.showMessageDialog(null, "Paralelo incorrecto. Selecciona un paralelo válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void votarCandidatoPorParalelo(String candidato, String paralelo) {
        votosPorParalelo.get(candidato).put(paralelo, votosPorParalelo.get(candidato).get(paralelo) + 1);
    }

    private static void actualizarEstadisticasAdmin() {
        actualizarEstadisticas("Estadísticas Descriptivas", votosPorCandidato, panelEstadisticas);
    }

    private static void actualizarEstadisticasPorParalelo() {
        for (String paralelo : paralelos) {
            Map<String, Integer> datosParalelo = votosPorParalelo.get(paralelo);

            if (datosParalelo != null) {
                actualizarEstadisticas("Estadísticas Descriptivas por Paralelo (" + paralelo + ")", datosParalelo, panelEstadisticas);
            } else {
                System.out.println("Data not found for parallel: " + paralelo);
            }
        }
    }

    private static void actualizarEstadisticas(String titulo, Map<String, ?> datos, Container contenedor) {
        contenedor.removeAll();

        JLabel labelEstadisticas = new JLabel(titulo);
        JTextArea areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);

        if (datos != null) {
            for (String clave : datos.keySet()) {
                int votos = (int) datos.get(clave);
                areaEstadisticas.append(clave + ": " + votos + " votos\n");
            }
        } else {
            System.out.println("Data is null for statistics: " + titulo);
        }

        contenedor.add(labelEstadisticas, BorderLayout.NORTH);
        contenedor.add(new JScrollPane(areaEstadisticas), BorderLayout.CENTER);

        contenedor.revalidate();
        contenedor.repaint();
    }

    private static void mostrarLider() {
        int maxVotos = 0;
        String lider = "";

        for (Candidato candidato : candidatos) {
            int votos = votosPorCandidato.get(candidato.toString());
            if (votos > maxVotos) {
                maxVotos = votos;
                lider = candidato.toString();
            }
        }

        JLabel labelLider = new JLabel("Candidato Líder: " + lider + " con " + maxVotos + " votos");
        panelEstadisticas.add(labelLider, BorderLayout.SOUTH);

        panelEstadisticas.revalidate();
        panelEstadisticas.repaint();
    }

    private static void agregarBotonFinalizar() {
        JButton btnFinalizar = new JButton("Finalizar");
        btnFinalizar.addActionListener(e -> determinarGanador());
        panelEstadisticas.add(btnFinalizar, BorderLayout.SOUTH);

        panelEstadisticas.revalidate();
        panelEstadisticas.repaint();
    }

    private static void determinarGanador() {
        int maxVotos = 0;
        String ganador = "";

        for (Candidato candidato : candidatos) {
            int votos = votosPorCandidato.get(candidato.toString());
            if (votos > maxVotos) {
                maxVotos = votos;
                ganador = candidato.toString();
            }
        }

        JOptionPane.showMessageDialog(null, "¡El candidato ganador es: " + ganador + " con " + maxVotos + " votos!");

        System.exit(0);
    }

    private static Color obtenerColorAleatorio() {
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }
}

class Candidato {
    private int id;
    private String nombre;
    private String apellido;
    private String partido;
    private int numeroLista;

    public Candidato(int id, String nombre, String apellido, String partido, int numeroLista) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.partido = partido;
        this.numeroLista = numeroLista;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getPartido() {
        return partido;
    }

    public int getNumeroLista() {
        return numeroLista;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + " " + apellido + ", Partido: " + partido + ", Número de Lista: " + numeroLista;
    }
}
