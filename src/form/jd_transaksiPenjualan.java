package form;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import tool.connection;

public class jd_transaksiPenjualan extends javax.swing.JDialog {
    Date now = new Date();
    SimpleDateFormat date_in = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat date_out = new SimpleDateFormat("dd/MM/yyyy");
    String status = "kosong";
    String chossen_trans = "";
    String chossen_detail = "";
    
    public jd_transaksiPenjualan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
        show_data();
    }
    
    public void filter(java.awt.event.KeyEvent evt){
        char karakter = evt.getKeyChar();
        if(!(((karakter >= '0') && (karakter <= '9') || (karakter == '.') || (karakter == KeyEvent.VK_BACK_SPACE) || (karakter == KeyEvent.VK_DELETE)))){
            getToolkit().beep();
            evt.consume();
        }
    }
    
    public void show_data(){
        int nomor = 1;
        DefaultTableModel table = new DefaultTableModel(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        DefaultTableCellRenderer tcr_center = new DefaultTableCellRenderer();
        tcr_center.setHorizontalAlignment(JLabel.CENTER);
        table.addColumn("No.");
        table.addColumn("Kode");
        table.addColumn("Tanggal");
        table.addColumn("Detail");
        try {
            Statement s = (Statement)connection.GetConnection().createStatement();
            Statement s1= (Statement)connection.GetConnection().createStatement();
            ResultSet r = s.executeQuery("select * from penjualan order by tanggal desc limit 100");
            while(r.next()){
                String detail = "";
                ResultSet rd = s1.executeQuery("select A.*,B.jenis from detail_penjualan as A "
                        + "inner join daging as B on A.kd_daging = B.kd_daging "
                        + "where A.kd_jual='"+r.getString("kd_jual")+"' order by A.kd_detail asc");
                while(rd.next()){
                    detail +=rd.getString("jenis")+" "+rd.getDouble("jumlah")+" Kg, ";
                }
                table.addRow(new Object[]{
                    nomor,
                    r.getString("kd_jual"),
                    date_out.format(r.getDate("tanggal")),
                    detail
                });
                nomor++;
            }
            jtData.setModel(table);
            jtData.getColumnModel().getColumn(0).setPreferredWidth(20);
            jtData.getColumnModel().getColumn(1).setPreferredWidth(60);
            jtData.getColumnModel().getColumn(2).setPreferredWidth(60);
            jtData.getColumnModel().getColumn(3).setPreferredWidth(900);
            jtData.getColumnModel().getColumn(0).setCellRenderer(tcr_center);
            jtData.getColumnModel().getColumn(1).setCellRenderer(tcr_center);
            jtData.getColumnModel().getColumn(2).setCellRenderer(tcr_center);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void show_detail(String a){
        int nomor = 1;
        DefaultTableModel table = new DefaultTableModel(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        DefaultTableCellRenderer tcr_center = new DefaultTableCellRenderer();
        DefaultTableCellRenderer tcr_right = new DefaultTableCellRenderer();
        tcr_center.setHorizontalAlignment(JLabel.CENTER);
        tcr_right.setHorizontalAlignment(JLabel.RIGHT);
        table.addColumn("No.");
        table.addColumn("Kode");
        table.addColumn("Jenis Daging");
        table.addColumn("Jumlah");
        try {
            Statement s = (Statement)connection.GetConnection().createStatement();
            Statement s1= (Statement)connection.GetConnection().createStatement();
            ResultSet r = s.executeQuery("select A.*,B.jenis from detail_penjualan as A "
                    + "inner join daging as B on A.kd_daging = B.kd_daging "
                    + "where A.kd_jual = '"+a+"' order by kd_detail desc limit 100");
            while(r.next()){
                table.addRow(new Object[]{
                    nomor,
                    r.getString("kd_detail"),
                    r.getString("jenis"),
                    r.getString("jumlah")
                });
                nomor++;
            }
            jtDetail.setModel(table);
            jtDetail.getColumnModel().getColumn(0).setPreferredWidth(20);
            jtDetail.getColumnModel().getColumn(1).setPreferredWidth(40);
            jtDetail.getColumnModel().getColumn(2).setPreferredWidth(750);
            jtDetail.getColumnModel().getColumn(3).setPreferredWidth(80);
            jtDetail.getColumnModel().getColumn(0).setCellRenderer(tcr_center);
            jtDetail.getColumnModel().getColumn(1).setCellRenderer(tcr_center);
            jtDetail.getColumnModel().getColumn(3).setCellRenderer(tcr_right);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void manage_form(){
        if(status.equals("edit")){
            jbBatal.setEnabled(true);
            jbHapus.setEnabled(true);
            jbSimpan.setEnabled(true);
            jbTambahDetail.setEnabled(true);
            jbRefreshDetail.setEnabled(true);
            jcbDaging.setEnabled(false);
        }else{
            populate_code();
            jbBatal.setEnabled(true);
            jbHapus.setEnabled(false);
            jbSimpan.setEnabled(true);
            jbTambahDetail.setEnabled(true);
            jbRefreshDetail.setEnabled(false);
            jcbDaging.setEnabled(true);
        }
        
        jdcTanggal.setEnabled(true);
        jtfJumlah.setEnabled(true);
        
        jtfKode.setEnabled(false);
        
        jdcTanggal.setDate(now);
        jtfJumlah.setText("0");
        
        show_data();
        populate_combo();
    }
    public void populate_code(){
        String code = null;
        int n = 1;
        boolean found = true;
        while(found){
            if(n < 10){
                code = "TJ-00000"+n;
            }else if(n < 100){
                code = "TJ-0000"+n;
            }else if(n < 1000){
                code = "TJ-000"+n;
            }else if(n < 10000){
                code = "TJ-00"+n;
            }else if(n < 100000){
                code = "TJ-0"+n;
            }else{
                code = "TJ-"+n;
            }
            try {
                Statement s = (Statement)connection.GetConnection().createStatement();
                Statement s1 = (Statement)connection.GetConnection().createStatement();
                ResultSet r = s.executeQuery("select kd_jual from penjualan "
                        + "where kd_jual='"+code+"'");
                if(r.next()){
                    n++;
                }else{
                    jtfKode.setText(code);
                    chossen_trans = code;
                    show_detail(code);
                    found = false;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public void populate_combo(){
        jcbDaging.removeAllItems();
        try {
            Statement s = (Statement)connection.GetConnection().createStatement();
            ResultSet r = s.executeQuery("select * from daging order by jenis asc");
            while(r.next()){
                jcbDaging.addItem(r.getString("kd_daging")+"-"+r.getString("jenis"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void save_data(){
        SimpleDateFormat f_tgl = new SimpleDateFormat("yyyy-MM-dd");
        boolean input = false;
        boolean in_tgl = false;
        String in_kode = jtfKode.getText();
        String in_tanggal = date_in.format(jdcTanggal.getDate());
        if(in_kode.equals("")){
            JOptionPane.showMessageDialog(null, "Lengkapi Inputan Data...", "Simpan", 1);
        }else{
            try {
                Date a = f_tgl.parse(f_tgl.format(now));
                Date b = f_tgl.parse(f_tgl.format(jdcTanggal.getDate()));
                Statement s = (Statement)connection.GetConnection().createStatement();
                Statement s1= (Statement)connection.GetConnection().createStatement();                
                if(!(status.equals("edit"))){
                    ResultSet r1 = s.executeQuery("select * from penjualan where tanggal='"+in_tanggal+"'");
                    if(r1.next()){
                        input = false;
                        in_tgl = true;
                    }else{
                        input = true;
                    }
                }
                if(input){
                    if (a.compareTo(b) < 0) {
                        input = false;
                        in_tgl = true;
                    }
                }
                if(input){
                    ResultSet r = s.executeQuery("select * from detail_penjualan where kd_jual='"+in_kode+"'");
                    if(r.next()){
                        input = true;
                    }else{
                        in_tgl = false;
                        input = false;
                    }
                }
                if(input){
                    if(status.equals("edit")){
                        s.executeUpdate("update penjualan set tanggal='"+in_tanggal+"' where kd_jual='"+in_kode+"'");
                        JOptionPane.showMessageDialog(null, "Perubahan data disimpan", "Simpan", 1);
                    }else{
                        s.executeUpdate("insert into penjualan values('"+in_kode+"','"+in_tanggal+"')");
                        JOptionPane.showMessageDialog(null, "Data disimpan", "Simpan", 1);
                    }
                    show_data();
                    jdDetail.setModal(false);
                    jdDetail.setVisible(false);
                }else{
                    if(in_tgl){
                        JOptionPane.showMessageDialog(null, "Tanggal tidak sesuai", "Simpan", 1);
                    }else{
                        JOptionPane.showMessageDialog(null, "Detail transaksi belum ada", "Simpan", 1);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public void add_data(){
        String in_jenis = ((String)jcbDaging.getSelectedItem()).substring(0,3);
        String in_jumlah = jtfJumlah.getText();
        if(in_jenis.equals("")||in_jumlah.equals("")||in_jumlah.equals("0")){
            JOptionPane.showMessageDialog(null, "Lengkapi Inputan Data...", "Tambah", 1);
        }else{
            try {
                Statement s = (Statement)connection.GetConnection().createStatement();
                Statement s1= (Statement)connection.GetConnection().createStatement();
                ResultSet r = s.executeQuery("select jumlah from detail_penjualan "
                    + "where kd_daging='"+in_jenis+"' and kd_jual='"+chossen_trans+"'");
                if(r.next()){
                    s1.executeUpdate("update detail_penjualan set jumlah='"+(r.getDouble("jumlah") + Double.parseDouble(in_jumlah))+"'"
                            + "where kd_daging='"+in_jenis+"' and kd_jual='"+chossen_trans+"'");
                }else{
                    s1.executeUpdate("insert into detail_penjualan values(null,'"+chossen_trans+"',"
                            + "'"+in_jenis+"','"+in_jumlah+"')");
                }
                show_detail(chossen_trans);
                populate_combo();
                jtfJumlah.setText("0");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public void reduce_data(){
        try {
            if(chossen_detail.equals("")){
                JOptionPane.showMessageDialog(null, "Data detail belum dipilih...", "Hapus", 1);
            }else{
                Statement s = (Statement)connection.GetConnection().createStatement();
                s.executeUpdate("delete from detail_penjualan where kd_detail='"+chossen_detail+"'");
                show_detail(chossen_trans);
                populate_combo();
                jtfJumlah.setText("0");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void change_data(){
        String in_jumlah = jtfJumlah.getText();
        try {
            if(chossen_detail.equals("")){
                JOptionPane.showMessageDialog(null, "Data detail belum dipilih...", "Hapus", 1);
            }else{
                Statement s = (Statement)connection.GetConnection().createStatement();
                Statement s1= (Statement)connection.GetConnection().createStatement();
                ResultSet r = s.executeQuery("select jumlah from detail_penjualan "
                    + "where kd_detail='"+chossen_detail+"'");
                if(r.next()){
                    /*s1.executeUpdate("update detail_penjualan set jumlah='"+(r.getDouble("jumlah") + Double.parseDouble(in_jumlah))+"'"
                            + "where kd_detail='"+chossen_detail+"'");*/
                    s1.executeUpdate("update detail_penjualan set jumlah='"+Double.parseDouble(in_jumlah)+"'"
                            + "where kd_detail='"+chossen_detail+"'");
                }
                show_detail(chossen_trans);
                populate_combo();
                jtfJumlah.setText("0");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void delete_data(){
        String in_kode = jtfKode.getText();
        int confirm = JOptionPane.showConfirmDialog(null, "Hapus data terpilih?", "Hapus", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            try {
                Statement s = (Statement)connection.GetConnection().createStatement();
                s.executeUpdate("delete from penjualan where kd_jual = '"+in_kode+"'");
                s.executeUpdate("delete from detail_penjualan where kd_jual = '"+in_kode+"'");
                JOptionPane.showMessageDialog(null, "Data dihapus", "Hapus", 1);
                show_data();
                jdDetail.setModal(false);
                jdDetail.setVisible(false);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public void cancel_data(){
        String in_kode = jtfKode.getText();
        try {
            Statement s = (Statement)connection.GetConnection().createStatement();
            s.executeUpdate("delete from penjualan where kd_jual = '"+in_kode+"'");
            s.executeUpdate("delete from detail_penjualan where kd_jual = '"+in_kode+"'");
            show_detail(chossen_trans);
            jdDetail.setModal(false);
            jdDetail.setVisible(false);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jdDetail = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jtfJumlah = new javax.swing.JTextField();
        jbBatal = new javax.swing.JButton();
        jbSimpan = new javax.swing.JButton();
        jbHapus = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jtfKode = new javax.swing.JTextField();
        jdcTanggal = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jbTambahDetail = new javax.swing.JButton();
        jbRefreshDetail = new javax.swing.JButton();
        jcbDaging = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtDetail = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jbHome = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtData = new javax.swing.JTable();
        jbTambah = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        jdDetail.setBounds(new java.awt.Rectangle(0, 0, 1200, 650));
        jdDetail.setUndecorated(true);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtfJumlah.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtfJumlah.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jtfJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtfJumlahKeyTyped(evt);
            }
        });
        jPanel2.add(jtfJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 100, 120, 25));

        jbBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_batal.png"))); // NOI18N
        jbBatal.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_over_batal.png"))); // NOI18N
        jbBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbBatalActionPerformed(evt);
            }
        });
        jPanel2.add(jbBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 220, 100, 27));

        jbSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_simpan.png"))); // NOI18N
        jbSimpan.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_over_simpan.png"))); // NOI18N
        jbSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbSimpanActionPerformed(evt);
            }
        });
        jPanel2.add(jbSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 100, 27));

        jbHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_hapus.png"))); // NOI18N
        jbHapus.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_over_hapus.png"))); // NOI18N
        jbHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbHapusActionPerformed(evt);
            }
        });
        jPanel2.add(jbHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 220, 100, 27));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(92, 76, 61));
        jLabel13.setText("Kode");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 110, 25));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(92, 76, 61));
        jLabel15.setText("Tanggal");
        jPanel2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 110, 25));

        jtfKode.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jtfKode.setEnabled(false);
        jPanel2.add(jtfKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 210, 25));

        jdcTanggal.setBackground(new java.awt.Color(255, 255, 255));
        jdcTanggal.setDateFormatString("dd/MM/yyyy");
        jdcTanggal.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcTanggalPropertyChange(evt);
            }
        });
        jPanel2.add(jdcTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 210, 25));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(92, 76, 61));
        jLabel18.setText("Jumlah");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 100, 110, 25));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(92, 76, 61));
        jLabel19.setText("Daging");
        jPanel2.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 60, 110, 25));

        jbTambahDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_tambahkan.png"))); // NOI18N
        jbTambahDetail.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_over_tambahkan.png"))); // NOI18N
        jbTambahDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbTambahDetailActionPerformed(evt);
            }
        });
        jPanel2.add(jbTambahDetail, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 100, 27, 27));

        jbRefreshDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_refresh.png"))); // NOI18N
        jbRefreshDetail.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_over_refresh.png"))); // NOI18N
        jbRefreshDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbRefreshDetailActionPerformed(evt);
            }
        });
        jPanel2.add(jbRefreshDetail, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 100, 27, 27));

        jPanel2.add(jcbDaging, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 60, 220, 25));

        jtDetail.setAutoCreateRowSorter(true);
        jtDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtDetail.setGridColor(new java.awt.Color(246, 246, 246));
        jtDetail.setRowHeight(25);
        jtDetail.getTableHeader().setResizingAllowed(false);
        jtDetail.getTableHeader().setReorderingAllowed(false);
        jtDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtDetailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtDetail);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 1140, 350));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/frame/frm_detail_penjualan.png"))); // NOI18N
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 650));

        javax.swing.GroupLayout jdDetailLayout = new javax.swing.GroupLayout(jdDetail.getContentPane());
        jdDetail.getContentPane().setLayout(jdDetailLayout);
        jdDetailLayout.setHorizontalGroup(
            jdDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jdDetailLayout.setVerticalGroup(
            jdDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Hasil Produksi");
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jbHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_kembali.png"))); // NOI18N
        jbHome.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_over_kembali.png"))); // NOI18N
        jbHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbHomeActionPerformed(evt);
            }
        });
        jPanel1.add(jbHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(1155, 10, 27, 27));

        jtData.setAutoCreateRowSorter(true);
        jtData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtData.setGridColor(new java.awt.Color(246, 246, 246));
        jtData.setRowHeight(25);
        jtData.getTableHeader().setResizingAllowed(false);
        jtData.getTableHeader().setReorderingAllowed(false);
        jtData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtDataMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtData);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 1140, 510));

        jbTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_tambah.png"))); // NOI18N
        jbTambah.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/button/btn_over_tambah.png"))); // NOI18N
        jbTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbTambahActionPerformed(evt);
            }
        });
        jPanel1.add(jbTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 100, 27));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/form/gambar/frame/frm__data_penjualan.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 650));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbTambahActionPerformed
        // TODO add your handling code here:
        status = "tambah";
        manage_form();
        jdDetail.setLocationRelativeTo(this);
        jdDetail.setModal(true);
        jdDetail.setVisible(true);
    }//GEN-LAST:event_jbTambahActionPerformed

    private void jbSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSimpanActionPerformed
        // TODO add your handling code here:
        save_data();
    }//GEN-LAST:event_jbSimpanActionPerformed

    private void jbHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbHapusActionPerformed
        // TODO add your handling code here:
        delete_data();
    }//GEN-LAST:event_jbHapusActionPerformed

    private void jbBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbBatalActionPerformed
        // TODO add your handling code here:
        if(status.equals("edit")){
            jdDetail.setModal(false);
            jdDetail.setVisible(false);
        }else{
            int konfirmasi = JOptionPane.showConfirmDialog(null, "Batalkan Transaksi", "Batal", JOptionPane.YES_NO_OPTION);
            if(konfirmasi == JOptionPane.YES_OPTION){
                cancel_data();
                show_data();
            }
        }
    }//GEN-LAST:event_jbBatalActionPerformed

    private void jbHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbHomeActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jbHomeActionPerformed

    private void jtDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtDataMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount() == 2){
            int i = jtData.getSelectedRow();
            if(i == -1){
                return;
            }

            status = "edit";
            manage_form();
            chossen_trans = (String)jtData.getValueAt(i, 1);
            try {
                Statement s = (Statement)connection.GetConnection().createStatement();
                ResultSet r = s.executeQuery("select * from penjualan where kd_jual='"+chossen_trans+"'");
                if(r.next()){
                    jtfKode.setText(r.getString("kd_jual"));
                    jdcTanggal.setDate(r.getDate("tanggal"));
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            show_detail(chossen_trans);
            jdDetail.setLocationRelativeTo(this);
            jdDetail.setModal(true);
            jdDetail.setVisible(true);
        }
    }//GEN-LAST:event_jtDataMouseClicked

    private void jdcTanggalPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcTanggalPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jdcTanggalPropertyChange

    private void jbTambahDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbTambahDetailActionPerformed
        // TODO add your handling code here:
        if(status.equals("edit")){
            change_data();
        }else{
            add_data();
        }
    }//GEN-LAST:event_jbTambahDetailActionPerformed

    private void jtDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtDetailMouseClicked
        // TODO add your handling code here:
        if(status.equals("edit")){
            if(evt.getClickCount() == 2){
                int i = jtDetail.getSelectedRow();
                if(i == -1){
                    return;
                }

                chossen_detail = (String)jtDetail.getValueAt(i, 1);
                try {
                    Statement s = (Statement)connection.GetConnection().createStatement();
                    ResultSet r = s.executeQuery("select A.*,B.* from detail_penjualan as A "
                            + "inner join daging as B on A.kd_daging = B.kd_daging "
                            + "where kd_detail='"+chossen_detail+"'");
                    if(r.next()){
                        jtfJumlah.setText(r.getString("jumlah"));
                        jcbDaging.setSelectedItem(r.getString("kd_daging")+"-"+r.getString("jenis"));
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }//GEN-LAST:event_jtDetailMouseClicked

    private void jbRefreshDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbRefreshDetailActionPerformed
        // TODO add your handling code here:
        chossen_detail = "";
        populate_combo();
        jtfJumlah.setText("0");
    }//GEN-LAST:event_jbRefreshDetailActionPerformed

    private void jtfJumlahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtfJumlahKeyTyped
        // TODO add your handling code here:
        filter(evt);
    }//GEN-LAST:event_jtfJumlahKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(jd_transaksiPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(jd_transaksiPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(jd_transaksiPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(jd_transaksiPenjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jd_transaksiPenjualan dialog = new jd_transaksiPenjualan(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbBatal;
    private javax.swing.JButton jbHapus;
    private javax.swing.JButton jbHome;
    private javax.swing.JButton jbRefreshDetail;
    private javax.swing.JButton jbSimpan;
    private javax.swing.JButton jbTambah;
    private javax.swing.JButton jbTambahDetail;
    private javax.swing.JComboBox jcbDaging;
    private javax.swing.JDialog jdDetail;
    private com.toedter.calendar.JDateChooser jdcTanggal;
    private javax.swing.JTable jtData;
    private javax.swing.JTable jtDetail;
    private javax.swing.JTextField jtfJumlah;
    private javax.swing.JTextField jtfKode;
    // End of variables declaration//GEN-END:variables
}
