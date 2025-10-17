import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import java.awt.Dialog;

import java.util.ArrayList;
import java.awt.Font;
import javax.swing.SwingConstants;

class mainGUI extends JFrame {

	// --- campos novos para exibir nome e pontuação ---
	private JLabel lblMyName = new JLabel("Você");
	private JLabel lblRightName = new JLabel("Direita");
	private JLabel lblUpName = new JLabel("Oponente");
	private JLabel lblLeftName = new JLabel("Esquerda");
	
	private JLabel lblMyScore = new JLabel("0");
	private JLabel lblRightScore = new JLabel("0");
	private JLabel lblUpScore = new JLabel("0");
	private JLabel lblLeftScore = new JLabel("0");



	private JPanel contentPane;
	JButton btnOpen;
	ArrayList<JLabel> table;
	private JPanel myPlayer;
	private JPanel myPlayerOpen;
	private JPanel playerRight;
	private JPanel playerLeft;
	private JPanel playerUpOpen;
	private JPanel playerUp;
	private JPanel playerRightOpen;
	private JPanel playerLeftOpen;
	private JPanel tablePanel;
	private JPanel throwPanel;
	private JLabel lblThrowtile;
	private JPanel windPanel;
	private JLabel lblWindgame = new JLabel();
	
	
	private ArrayList<Tile> rightPlayerOpenTile;
	private ArrayList<Tile> upPlayerOpenTile;
	private ArrayList<Tile> leftPlayerOpenTile;
	private ArrayList<Tile> myPlayerOpenTile;
	private ArrayList<Tile> tableTile;
	private ArrayList<Tile> myPlayerHandTile;
	
	private int numRightPlayer, numUpPlayer, numLeftPlayer;
	
	private boolean[] choice = {false, false, false, false, false, false}; /*choose 吃 碰 槓 聽 胡 不要*/ // Coma, toque, ouça, ouça, não
	private boolean[] select = {false, false, false, false, false}; /*you can choose 吃 碰 槓 聽 胡*/ // Coma, toque e ouça
	private ArrayList<ArrayList<Tile>> chewChoice;
	
	private int flipNum;
	private ArrayList<Tile> rightPlayerHandTile;
	private ArrayList<Tile> upPlayerHandTile;
	private ArrayList<Tile> leftPlayerHandTile;
	
	private int thrower;
	private Tile newTile;
	
	int wind = 0, game = 0;
	
	public volatile boolean ok;
	public volatile boolean nok;
	public ArrayList<Tile> push;
	public boolean restart;

	// Alteração Ibanez

	private JPanel xPanel;
	private JDialog xDialog;

	
	/**
	 * Launch the application.
	 */
	public void start()
	{
		this.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public mainGUI() {
		flipNum = -1;
		
		ok = false;
		push = new ArrayList<Tile>();
		
		rightPlayerOpenTile = new ArrayList<Tile>();
		upPlayerOpenTile = new ArrayList<Tile>();
		leftPlayerOpenTile = new ArrayList<Tile>();
		myPlayerOpenTile = new ArrayList<Tile>();
		tableTile = new ArrayList<Tile>();
		myPlayerHandTile = new ArrayList<Tile>();
		
		numRightPlayer = 0;
		numUpPlayer = 0;
		numLeftPlayer = 0;
		
		
		this.setTitle("POOMahjong");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 10, 796, 703);
		reset();
	}
	
	public void renew()
	{
		contentPane.revalidate();
		contentPane.repaint();
	}
	public void changeEnable(boolean b)
	{
		for(Component component : ((Container)myPlayer).getComponents()) {
		    component.setEnabled(b);
		}
	}
	public void addButton(JPanel panel, JPanel tablePanel, int suit, int value)
	{
		JToggleButton button = new JToggleButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToBoard(suit, value);
				showThrowTile(false);
			}
		});
		button.setIcon(decideIcon(suit, value, false));
		button.setSelectedIcon(decideIcon(0, 0, false));
		button.setPreferredSize(new java.awt.Dimension(30, 37));
		panel.add(button);
	}
	public void addButton(int suit, int value)
	{
		addButton(myPlayer, tablePanel, suit, value);
	}
	public JButton addButton(String name, int index)
	{
		JPanel panel = this.getPanel();
		JDialog dialog = this.getDialog();
		JButton rdbtnNewRadioButton = new JButton(name);
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < choice.length; i++)
					choice[i] = false;
				choice[index] = true;
				
				doChoice(choice, panel);
				panel.revalidate();
				panel.repaint();
				dialog.dispose();
			}
		});
		rdbtnNewRadioButton.setBounds(592, 246, 107, 23);
		panel.add(rdbtnNewRadioButton);
		return rdbtnNewRadioButton;
	}
	public void removeButton(JPanel panel, JButton button)
	{
		panel.remove(button);
		panel.revalidate();
		panel.repaint();
	}
	public void removeButton(JPanel panel, JToggleButton button)
	{
		panel.remove(button);
		panel.revalidate();
		panel.repaint();
	}
	
	public void removeLabel(JPanel panel, int index)
	{
		panel.remove(index);
		panel.revalidate();
		panel.repaint();
	}
	public void removeLabel(JPanel panel, JLabel label)
	{
		panel.remove(label);
		panel.revalidate();
		panel.repaint();
	}
	public void addLabel(JPanel panel, int suit, int value, boolean fall)
	{
		JLabel label = new JLabel("");
			
		label.setIcon(decideIcon(suit, value, fall));
		if(fall)
			label.setPreferredSize(new java.awt.Dimension(37, 30));
		else
			label.setPreferredSize(new java.awt.Dimension(30, 37));
		panel.add(label);
	}
	public ImageIcon decideIcon(int suit, int value, boolean fall)
	{
		String filePath = "./icon";
		if(value == 0)
			filePath += "/cover";
		else if(suit == 0)
			filePath += "/character_" + value;
		else if(suit == 1)
			filePath += "/dot_" + value;
		else if(suit == 2)
			filePath += "/bamboo_" + value;
		else if(suit == 3){
			if(value < 5)
				filePath += "/wind_" + value;
			else
				filePath += "/dragon_" + (value % 4);
		}
		if(fall)
			filePath += "_fall.png";
		else
			filePath += ".png";
		return (new ImageIcon(mainGUI.class.getResource(filePath)));
	}
	
	public void frameOpen()
	{
		boolean flag = false;
		for(int i = 0; i < 5; i++)
			flag |= select[i];
		if(flag){
	        JDialog dialog = new JDialog ();
	        dialog.setTitle("Por favor escolha"); //請選擇 - Por favor escolha
	        dialog.setModal (true);
	        dialog.setAlwaysOnTop (true);
	        dialog.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
	        dialog.setDefaultCloseOperation (JDialog.DO_NOTHING_ON_CLOSE);
	        dialog.setBounds(100, 100, 310, 221);
	        
			JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			dialog.setContentPane(panel);
			panel.setLayout(null);
			
	        JPanel panel_1 = new JPanel();
			panel_1.setBounds(0, 0, 294, 134);
			panel.add(panel_1);
			panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	        
			JPanel panel_2 = new JPanel();
			panel_2.setBounds(0, 139, 294, 44);
			panel.add(panel_2);
			panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			
			String s = "Você tocou"; // Você tocou 你摸了
			if(thrower == 1)
				s = "Seu próximo está derrotado"; // Seu próximo está derrotado 你的下家打了
			else if(thrower == 2)
				s = "Seu oponente acertou"; // Seu oponente acertou 你的對家打了
			else if(thrower == 3)
				s = "Sua última família foi espancada"; // Sua última família foi espancada 你的上家打了
			panel_2.add(new JLabel(s));
			addLabel(panel_2, newTile.suit, newTile.value+1, false);
			
			// Mudança 01

			
			createButton(panel_1, dialog);
			dialog.setVisible (true);
		}
	}
	
	public void createButton(JPanel panel, JDialog dialog)
	{
		this.setPanel(panel); 
		this.setDialog(dialog);
		
		ButtonGroup group = new ButtonGroup();
			
		// Mudança 02

		if(select[0])
			group.add(addButton("comer", 0)); // comer 吃
		if(select[1])
			group.add(addButton("ressalto", 1)); // ressalto 碰
		if(select[2])
			group.add(addButton("barra", 2)); // barra 槓
		if(select[3])
			group.add(addButton("ouvir", 3)); // ouvir 聽
		if(select[4])
			group.add(addButton("Hu", 4)); // Hu 胡
		
		group.add(addButton("Não quero", 5)); // não quero 不要
		
		boolean[] b = {false, false, false, false, false};
		setSelect(b);
		
	}
	
	public void createButtonGroup(JPanel panel)
	{
		ButtonGroup group = new ButtonGroup();
		if(select[0])
			group.add(addRadioButton(panel, "comer", 0, choice)); // comer 吃
		if(select[1])
			group.add(addRadioButton(panel, "ressalto", 1, choice)); // ressalto 碰
		if(select[2])
			group.add(addRadioButton(panel, "barra", 2, choice)); // barra 槓
		if(select[3])
			group.add(addRadioButton(panel, "ouvir", 3, choice)); // ouvir 聽
		if(select[4])
			group.add(addRadioButton(panel, "Hu", 4, choice)); // Hu 胡
		
		group.add(addRadioButton(panel, "não quero", 5, choice)); // não quero 不要
		
		boolean[] b = {false, false, false, false, false};
		setSelect(b);
		
	}
	
	public JRadioButton addRadioButton(JPanel panel, String name, int index, boolean[] choice)
	{
		JRadioButton rdbtnNewRadioButton = new JRadioButton(name);
		rdbtnNewRadioButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				for(int i = 0; i < choice.length; i++)
					choice[i] = false;
				choice[index] = true;
			}
		});
		rdbtnNewRadioButton.setBounds(592, 246, 107, 23);
		panel.add(rdbtnNewRadioButton);
		return rdbtnNewRadioButton;
	}
	public void setSelect(boolean[] _select)
	{
		for(int i = 0; i < 5; i++)
			select[i] = _select[i];
	}
	
	public void reset()
	{
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new ArrayList<JLabel>();
		
		tablePanel = new JPanel();
		tablePanel.setBackground(new Color(0, 100, 0));
		tablePanel.setBounds(137, 114, 499, 435);
		contentPane.add(tablePanel);
		tablePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		myPlayer = new JPanel();
		myPlayer.setBounds(137, 611, 499, 42);
		contentPane.add(myPlayer);
		myPlayer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		myPlayerOpen = new JPanel();
		myPlayerOpen.setBounds(137, 559, 499, 42);
		contentPane.add(myPlayerOpen);
		
		playerRight = new JPanel();
		playerRight.setBounds(714, 62, 56, 499);
		contentPane.add(playerRight);
		
		playerLeft = new JPanel();
		playerLeft.setBounds(5, 62, 56, 499);
		contentPane.add(playerLeft);
		
		playerUpOpen = new JPanel();
		playerUpOpen.setBounds(137, 62, 499, 42);
		contentPane.add(playerUpOpen);
		playerUpOpen.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		playerUp = new JPanel();
		playerUp.setBounds(137, 18, 499, 42);
		contentPane.add(playerUp);
		
		playerRightOpen = new JPanel();
		playerRightOpen.setBounds(648, 62, 56, 499);
		contentPane.add(playerRightOpen);
		
		playerLeftOpen = new JPanel();
		playerLeftOpen.setBounds(71, 62, 56, 499);
		contentPane.add(playerLeftOpen);
		
		throwPanel = new JPanel();
		throwPanel.setBounds(646, 571, 124, 84);
		contentPane.add(throwPanel);
		throwPanel.setLayout(null);
		
		windPanel = new JPanel();
		windPanel.setBounds(5, 571, 124, 82);
		contentPane.add(windPanel);
		windPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// -- painéis auxiliares para exibir nomes e scores --
		JPanel myInfoPanel = new JPanel();
		myInfoPanel.setBounds(137, 640, 499, 30); // logo abaixo do myPlayer
		myInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		lblMyName.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblMyScore.setFont(new Font("Verdana", Font.BOLD, 12));
		myInfoPanel.add(lblMyName);
		myInfoPanel.add(new JLabel(" - "));
		myInfoPanel.add(lblMyScore);
		contentPane.add(myInfoPanel);
			
		JPanel upInfoPanel = new JPanel();
		upInfoPanel.setBounds(137, 2, 499, 30); // acima do playerUp
		upInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
		lblUpName.setFont(new Font("Verdana", Font.PLAIN, 12));
		lblUpScore.setFont(new Font("Verdana", Font.BOLD, 12));
		upInfoPanel.add(lblUpName);
		upInfoPanel.add(new JLabel(" - "));
		upInfoPanel.add(lblUpScore);
		contentPane.add(upInfoPanel);
			
		JPanel leftInfoPanel = new JPanel();
		leftInfoPanel.setBounds(5, 30, 60, 30); // ao lado esquerdo
		leftInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		lblLeftName.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblLeftScore.setFont(new Font("Verdana", Font.BOLD, 11));
		leftInfoPanel.add(lblLeftName);
		leftInfoPanel.add(new JLabel(" "));
		leftInfoPanel.add(lblLeftScore);
		contentPane.add(leftInfoPanel);
			
		JPanel rightInfoPanel = new JPanel();
		rightInfoPanel.setBounds(714, 30, 60, 30); // ao lado direito
		rightInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		lblRightName.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblRightScore.setFont(new Font("Verdana", Font.BOLD, 11));
		rightInfoPanel.add(lblRightName);
		rightInfoPanel.add(new JLabel(" "));
		rightInfoPanel.add(lblRightScore);
		contentPane.add(rightInfoPanel);

		//lblWindgame = new JLabel("AAA");
		lblWindgame.setForeground(Color.DARK_GRAY);
		lblWindgame.setFont(new Font("Verdana", Font.PLAIN, 10)); // Fonte preta da Microsoft
		lblWindgame.setHorizontalAlignment(SwingConstants.CENTER);
		lblWindgame.setBounds(21, 20, 85, 40);
		windPanel.add(lblWindgame);
		
		refreshAllContent();
		contentPane.revalidate();
		contentPane.repaint();
	}
	public void hu(int type, int from)
	{   
		JDialog dialog = new JDialog ();
        dialog.setModal (true);
        dialog.setAlwaysOnTop (true);
        dialog.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation (JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setBounds(100, 100, 310, 221);
        
        
        JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(null);
		dialog.setContentPane(panel);
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 294, 134);
		panel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		String s = "";
		if(type == 0){
			s = "Perdido!"; // Perdido! 流局!
		}
		else{
			if(from == 0)
				s = "Você"; // você 你
			else if(from == 1)
				s = "Sua próxima casa"; // sua próxima casa 你的下家
			else if(from == 2)
				s = "Seu oponente"; // seu oponente 你的對家
			else
				s = "Sua última família"; // Sua última família 你的上家
			
			if(type == 1)
				s += "Orgulhoso!"; // Orgulhoso! 榮了!
			else
				s += "Que tolo!"; // Que tolo! 胡了!
		}
		panel_1.add(new JLabel(s));

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 139, 294, 44);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		JButton button = new JButton("confirme"); // confirme 確認
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reset();
				dialog.dispose();
			}
		});
		button.setBounds(102, 10, 87, 23);
		panel_2.add(button);
		
		dialog.setVisible(true);
	}
	public void clear()
	{
		int length = tablePanel.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(tablePanel, 0);
		}
		length = myPlayer.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(myPlayer, 0);
		}
		length = myPlayerOpen.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(myPlayerOpen, 0);
		}
		length = playerRight.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(playerRight, 0);
		}
		length = playerLeft.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(playerLeft, 0);
		}
		length = playerUp.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(playerUp, 0);
		}
		length = playerRightOpen.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(playerRightOpen, 0);
		}
		length = playerLeftOpen.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(playerLeftOpen, 0);
		}
		length = playerUpOpen.getComponentCount();
		for(int i = 0; i < length; i++){
			removeLabel(playerUpOpen, 0);
		}
	}
	
	public void setAllContent(ArrayList<ArrayList<Tile>> temp, int[] tempNum)
	{
		tableTile = temp.get(0);
		myPlayerOpenTile = temp.get(1);
		rightPlayerOpenTile = temp.get(2);
		upPlayerOpenTile = temp.get(3);
		leftPlayerOpenTile = temp.get(4);
		myPlayerHandTile = temp.get(5);
		
		numRightPlayer = tempNum[0];
		numUpPlayer = tempNum[1];
		numLeftPlayer = tempNum[2];
	}
	
	public void refreshAllContent()
	{
		clear();
		if(flipNum == 0)
			for(int i = 0; i < rightPlayerHandTile.size(); i++)
				addLabel(playerRight, rightPlayerHandTile.get(i).suit, rightPlayerHandTile.get(i).value + 1, true);
		else{
			for(int i = 0; i < numRightPlayer; i++)
				addLabel(playerRight, 0, 0, true);
		}
		if(flipNum == 1){
			for(int i = 0; i < upPlayerHandTile.size(); i++)
				addLabel(playerUp, upPlayerHandTile.get(i).suit, upPlayerHandTile.get(i).value + 1, false);
		}
		else{
			for(int i = 0; i < numUpPlayer; i++)
				addLabel(playerUp, 0, 0, false);
		}
		if(flipNum == 2){
			for(int i = 0; i < leftPlayerHandTile.size(); i++)
				addLabel(playerLeft, leftPlayerHandTile.get(i).suit, leftPlayerHandTile.get(i).value + 1, true);
		}
		else{
			for(int i = 0; i < numLeftPlayer; i++)
				addLabel(playerLeft, 0, 0, true);
		}
		
		for(int i = 0; i < tableTile.size(); i++)
			addLabel(tablePanel, tableTile.get(i).suit, tableTile.get(i).value + 1, false);
		for(int i = 0; i < rightPlayerOpenTile.size(); i++)
			addLabel(playerRightOpen, rightPlayerOpenTile.get(i).suit, rightPlayerOpenTile.get(i).value + 1, true);
		for(int i = 0; i < upPlayerOpenTile.size(); i++)
			addLabel(playerUpOpen, upPlayerOpenTile.get(i).suit, upPlayerOpenTile.get(i).value + 1, false);
		for(int i = 0; i < leftPlayerOpenTile.size(); i++)
			addLabel(playerLeftOpen, leftPlayerOpenTile.get(i).suit, leftPlayerOpenTile.get(i).value + 1, true);
		for(int i = 0; i < myPlayerOpenTile.size(); i++)
			addLabel(myPlayerOpen, myPlayerOpenTile.get(i).suit, myPlayerOpenTile.get(i).value + 1, false);
		for(int i = 0; i < myPlayerHandTile.size(); i++)
			addButton(myPlayerHandTile.get(i).suit, myPlayerHandTile.get(i).value + 1);
		
		renew();
	}
	
	public void sendToBoard(int suit, int value)
	{
		Tile t = new Tile(suit*9 + (value - 1));
		ArrayList<Tile> temp = new ArrayList<Tile>();
		temp.add(t);
		push = temp;
		ack();
	}
	public void ack()
	{
		ok = true;
	}
	public void actionFail()
	{
		JDialog dialog = new JDialog ();
        dialog.setModal (true);
        dialog.setAlwaysOnTop (true);
        dialog.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation (JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setBounds(100, 100, 310, 221);
        
        JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(null);
		dialog.setContentPane(panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 294, 134);
		panel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		panel_1.add(new JLabel("Alguém tem prioridade maior que você e a execução falhou!")); // Alguém tem prioridade maior que você e a execução falhou! 有人的優先權比你高,執行失敗!
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 139, 294, 44);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		JButton button = new JButton("Confirme"); // confirme 確認
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		button.setBounds(102, 10, 87, 23);
		panel_2.add(button);
		
		dialog.setVisible(true);
	}
	public void doChoice(boolean[] choice, JPanel panel_1)
	{
		if(choice[0]){
			chewOptionFrame();
		}
		else{
			ack();
		}
	}
	public void setChowOption(int flag, ArrayList<ArrayList<Tile>> _chewChoice)
	{
		chewChoice = _chewChoice;
	}
	public boolean[] getChoice()
	{
		return choice;
	}
	
	public void chewOptionFrame()
	{
		if(chewChoice.size() > 1){
			JDialog dialog = new JDialog ();
	        dialog.setTitle("Qual comer"); // Qual comer 要吃哪一種
	        dialog.setModal (true);
	        dialog.setAlwaysOnTop (true);
	        dialog.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
	        dialog.setDefaultCloseOperation (JDialog.DO_NOTHING_ON_CLOSE);
	        dialog.setBounds(100, 100, 310, 221);
	        
	        JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			dialog.setContentPane(panel);
			panel.setLayout(null);
			
			
			JPanel panel_1 = new JPanel();
			panel_1.setBounds(0, 0, 294, 134);
			panel.add(panel_1);
			panel_1.setLayout(null);
			
			JPanel[] panel_1_ = new JPanel[3];
			for(int i = 0; i < 3; i++)
				panel_1_[i] = new JPanel();
			panel_1_[0] = new JPanel();
			panel_1_[0].setBounds(10, 10, 274, 38);
			panel_1_[0].setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			panel_1.add(panel_1_[0]);
			panel_1_[1] = new JPanel();
			panel_1_[1].setBounds(10, 49, 274, 38);
			panel_1_[1].setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			panel_1.add(panel_1_[1]);
			panel_1_[2] = new JPanel();
			panel_1_[2].setBounds(10, 87, 274, 38);
			panel_1_[2].setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
			panel_1.add(panel_1_[2]);
			
			boolean[] cChoice = {false, false, false};
			ButtonGroup group = new ButtonGroup();
			
			for(int i = 0; i < chewChoice.size(); i++){
				group.add(addRadioButton(panel_1_[i], "", i, cChoice));
				for(Tile temp : chewChoice.get(i))
					addLabel(panel_1_[i], temp.suit, temp.value + 1, false);
			}
			
			JPanel panel_2 = new JPanel();
			panel_2.setBounds(0, 139, 294, 44);
			panel.add(panel_2);
			panel_2.setLayout(null);
			
			JButton button = new JButton("Confirme"); // confirme 確認
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(cChoice[0] || cChoice[1] || cChoice[2]){
						dialog.dispose();
						for(int i = 0; i < 3; i++)
							if(cChoice[i])
								push = chewChoice.get(i);
						ack();
					}
					
				}
			});
			button.setBounds(102, 10, 87, 23);
			panel_2.add(button);
			dialog.setVisible(true);
		}
		else
			ack();
	}
	public void setThrower(int _thrower, Tile _newTile)
	{
		thrower = _thrower;
		newTile = _newTile;
	}

	public void setFlip(int num, ArrayList<Tile> temp)
	{
		flipNum = num;
		if(num == 0)
			rightPlayerHandTile = temp;
		else if(num == 1)
			upPlayerHandTile = temp;
		else if(num == 2)
			leftPlayerHandTile = temp;
	}
	public void resetChoice()
	{
		for(int i = 0; i < 6; i++)
			choice[i] = false;
	}
	public void showThrowTile(boolean throwTile)
	{
		if(throwTile){
			lblThrowtile = new JLabel("Por favor, jogue suas cartas"); // Por favor, jogue suas cartas 請出牌
			lblThrowtile.setFont(new Font("Verdana", Font.BOLD | Font.ITALIC, 10)); // Fonte preta da Microsoft
			lblThrowtile.setForeground(Color.RED);
			lblThrowtile.setBounds(31, 23, 70, 35);
			throwPanel.add(lblThrowtile);
		}
		else{
			removeLabel(throwPanel, lblThrowtile);
		}
	}
	public void showWind(int wind, int game)
	{
		this.wind = wind;
		this.game = game;
		String[] windString = {"Leste", "Sul", "Oeste", "Norte"}; // Leste, Sul, Oeste, norte {"東", "南", "西", "北"}
		String s;
		if(game == -1){
			s = "Fim de Jogo"; // game Over ou Fim de Jogo {遊戲結束}
			showGameOverButtons();
		}
		else{
			s = windString[wind] + " " + game + " escritório"; // escritório {局}
		}
		lblWindgame.setText(s);
	}

	// Novo método para mostrar botões de fim de jogo
	private void showGameOverButtons() {
		// Limpa o painel primeiro
		throwPanel.removeAll();
		
		JButton btnPlayAgain = new JButton("Jogar Novamente");
		btnPlayAgain.setBounds(10, 10, 104, 25);
		btnPlayAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restart = true;
				nok = true;
				// Limpa os botões
				throwPanel.removeAll();
				throwPanel.revalidate();
				throwPanel.repaint();
			}
		});
		throwPanel.add(btnPlayAgain);
		
		JButton btnExit = new JButton("Sair");
		btnExit.setBounds(10, 45, 104, 25);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		throwPanel.add(btnExit);
		
		throwPanel.revalidate();
		throwPanel.repaint();
	}

	/**
	 * Define nomes dos jogadores.
	 * names: array de 4 strings na ordem {meu, direita, oponente, esquerda}
	 */
	public void setPlayerNames(String[] names) {
	    if (names == null || names.length < 4) return;
	    lblMyName.setText(names[0]);
	    lblRightName.setText(names[1]);
	    lblUpName.setText(names[2]);
	    lblLeftName.setText(names[3]);
	}

	/**
	 * Define pontuações iniciais.
	 * scores: array de 4 inteiros na ordem {meu, direita, oponente, esquerda}
	 */
	public void setScores(int[] scores) {
	    if (scores == null || scores.length < 4) return;
	    lblMyScore.setText(Integer.toString(scores[0]));
	    lblRightScore.setText(Integer.toString(scores[1]));
	    lblUpScore.setText(Integer.toString(scores[2]));
	    lblLeftScore.setText(Integer.toString(scores[3]));
	}

	/**
	 * Atualiza a pontuação de um jogador específico.
	 * index: 0=me, 1=right, 2=up, 3=left
	 */
	public void updateScore(int index, int value) {
	    switch(index) {
	        case 0: lblMyScore.setText(Integer.toString(value)); break;
	        case 1: lblRightScore.setText(Integer.toString(value)); break;
	        case 2: lblUpScore.setText(Integer.toString(value)); break;
	        case 3: lblLeftScore.setText(Integer.toString(value)); break;
	    }
	}


	// Metodos Genericos
	
	private JDialog getDialog() {
		return this.xDialog;
	}

	private void setDialog(JDialog wDialog) {
		this.xDialog = wDialog;
	}
	
	private JPanel getPanel() {
		return this.xPanel;
	}

	private void setPanel(JPanel wPanel) {
		this.xPanel = wPanel;
	}
	
}
