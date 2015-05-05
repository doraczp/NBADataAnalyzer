package pres.playerui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import constantinfo.Constant;
import constantinfo.SortBy;
import bl.playerbl.PlayerBL;
import bl.teambl.TeamBL;
import blservice.playerblservice.PlayerBLService;
import pres.playerui.SeasonHotPlayerPanel.tableadapter;
import pres.uitools.CommonButton;
import pres.uitools.CommonPanel;
import pres.uitools.CommonTable;
import vo.playerVO;
import vo.teamVO;

public class MostProgressedPanel extends CommonPanel{
	JComboBox ScreenBy;
	JLabel playerinfo;
	JScrollPane scroll;
	CommonButton back;
	CommonButton screen;
	CommonTable result;
	
	ArrayList<playerVO> firstfive;
	ArrayList<playerVO> playervolist;
	PlayerBLService playerblservice;

	public MostProgressedPanel(ArrayList<playerVO> playervolist) {
		super("graphics/detailpanel/detail_background.png");
		Font font1=new Font("微软雅黑",Font.BOLD,12);
		this.playervolist=playervolist;
		firstfive=new ArrayList<playerVO>();
		playerblservice=new PlayerBL();
		
		playerinfo=new JLabel(new ImageIcon("graphics/detailpanel/playerinfo_label.png"));
		playerinfo.setBounds(500, 20, 180, 45);
		playerinfo.setVisible(true);
		functionlabel.add(playerinfo);
		
		String[] screenby={"场均得分","场均篮板","场均助攻"};
		ScreenBy=new JComboBox(screenby);
		ScreenBy.setBounds(452, 150, 120, 35);
		ScreenBy.setFont(font1);
		ScreenBy.setBorder(BorderFactory.createEmptyBorder());
		ScreenBy.setSelectedItem(null);
		ScreenBy.setVisible(true);
		functionlabel.add(ScreenBy);
		
		addTable(0);//默认以场均得分排序
		
		screen=new CommonButton("graphics/actionbutton/screen.png","graphics/actionbutton/screen_dark_pressed.png","graphics/actionbutton/screen_dark_pressed.png");
		screen.setBounds(600, 150, 240, 60);
		screen.addActionListener(new screenlistener());
		screen.setVisible(true);
		functionlabel.add(screen);
		
		back=new CommonButton("graphics/actionbutton/back_pressed.png","graphics/actionbutton/back_pressed.png","graphics/actionbutton/back.png");
		back.setBounds(392, 600, 240, 60);
		back.setVisible(true);
		back.addActionListener(new backlistener());
		functionlabel.add(back);
		// TODO Auto-generated constructor stub
	}
	
	public void addTable(int index){
		SortBy sortby=SortBy.PROGRESS_AVERAGE_SCORE;
		ArrayList<playerVO> list2sort=new ArrayList<playerVO>();
		//计算提升率
		switch(index){
		case 0:{
			sortby=SortBy.PROGRESS_AVERAGE_SCORE;
			for(playerVO playervo:playervolist){
				if(playervo.recentFive.size()<6)
					continue;
				else{
					//计算五场之前的数据
					int totalscorebefore=playervo.totalScores;	
					for(int i=0;i<5;i++){
						totalscorebefore=totalscorebefore-playervo.recentFive.get(i).getScore();
					}
					
					if(totalscorebefore<=0)
						continue;
					else{
						double A=(double)totalscorebefore/(double)playervo.totalMatches-5;
						double B=(double)(playervo.totalScores-totalscorebefore)/5.0;
						playervo.progress=(B-A)/A;
					}
				}
			}
			break;
			}
		case 1:{
			sortby=SortBy.PROGRESS_AVERAGE_REBOUND;
			for(playerVO playervo:playervolist){
				if(playervo.recentFive.size()<6)
					continue;
				else{
					//计算五场之前的数据
					int totalreboundbefore=playervo.totalRebounds;	
					for(int i=0;i<5;i++){
						totalreboundbefore=totalreboundbefore-playervo.recentFive.get(i).getRebounds();
					}
					
					if(totalreboundbefore<=0)
						continue;
					else{
						double A=(double)totalreboundbefore/(double)playervo.totalMatches-5;
						double B=(double)(playervo.totalRebounds-totalreboundbefore)/5.0;
						playervo.progress=(B-A)/A;
					}
				}
			}
			break;}
		case 2:{sortby=SortBy.PROGRESS_AVERAGE_ASSIST;
		for(playerVO playervo:playervolist){
			if(playervo.recentFive.size()<6)
				continue;
			else{
				//计算五场之前的数据
				int totalassistbefore=playervo.totalAssists;	
				for(int i=0;i<5;i++){
					totalassistbefore=totalassistbefore-playervo.recentFive.get(i).getAssists();
				}
				
				if(totalassistbefore<=0)
					continue;
				else{
					double A=(double)totalassistbefore/(double)playervo.totalMatches-5;
					double B=(double)(playervo.totalAssists-totalassistbefore)/5.0;
					playervo.progress=(B-A)/A;
				}
			}
		}
			break;}
		default:{}
		}
		
		playervolist=playerblservice.sortBy(sortby, false,playervolist);
		
		if(firstfive.size()==0){
			for(int i=0;i<5;i++){
				firstfive.add(playervolist.get(i));
			}
		}
		
		//firstfive=(ArrayList<teamVO>) teamblservice.sortBy(sortby, false,teamvolist).subList(0, 5);
		Object[] resultcolomn={"球员名称","球员所属联盟","球员场上位置","场均得分","场均篮板","场均助攻","场均盖帽","场均抢断","三分命中率","投篮命中率","罚球命中率"};
		Object[][] resultdetail=new Object[5][11];
		
		int length=0;
		for(playerVO singleplayer:firstfive){
			resultdetail[length][0]=singleplayer.name;
			resultdetail[length][1]=singleplayer.league;
			resultdetail[length][2]=singleplayer.position;
			resultdetail[length][3]=String.format("%.2f", (double)singleplayer.totalScores/(double)singleplayer.totalMatches);
			resultdetail[length][4]=String.format("%.2f", (double)singleplayer.totalRebounds/(double)singleplayer.totalMatches);
			resultdetail[length][5]=String.format("%.2f", (double)singleplayer.totalAssists/(double)singleplayer.totalMatches);
			resultdetail[length][6]=String.format("%.2f", (double)singleplayer.totalRejection/(double)singleplayer.totalMatches);
			resultdetail[length][7]=String.format("%.2f", (double)singleplayer.totalSteals/(double)singleplayer.totalMatches);
			resultdetail[length][8]=String.format("%.2f", singleplayer.shotPercent);
			resultdetail[length][9]=String.format("%.2f", singleplayer.threePointPercent);
			resultdetail[length][10]=String.format("%.2f", singleplayer.freeThrowPercent);

			length++;
		}
		result=new CommonTable(resultdetail,resultcolomn);
		result.setPreferredScrollableViewportSize(new Dimension(550,180));
		result.setRowHeight(30);
		result.setFont(new Font("微软雅黑",Font.BOLD,15));
		//FitTableColumns(table);
		
		/*for(int i=0;i<allmatches.getColumnCount();i++){
			TableColumn tc=allmatches.getColumn(allmatches.getColumnName(i));  
	        tc.setMinWidth(150);

		}*/
		result.addMouseListener(new tableadapter());
		result.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//result.getTableHeader().setPreferredSize(new Dimension (result.getTableHeader().getMinimumSize().width,30));
		result.updateUI();

		scroll = new JScrollPane();
		scroll.setViewportView(result);
		scroll.setLocation(312,350);
		scroll.setSize(550, 200);
		scroll.setVisible(true);
		functionlabel.add(scroll);
	}
	
	class tableadapter implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()==2){
				int index=result.getSelectedRow();
				Constant.mainframe.showSinglePlayerPanel(firstfive.get(index));
			}
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class screenlistener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			functionlabel.remove(result);
			functionlabel.remove(scroll);
			
			addTable(ScreenBy.getSelectedIndex());
			Constant.mainframe.repaint();
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class backlistener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			Constant.mainframe.showPlayerMainPanel();
			// TODO Auto-generated method stub
			
		}
		
	}
	
	

}
