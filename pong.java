package pong;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Pong implements ActionListener, KeyListener
{

	public static Pong pong;

	public int width = 700, height = 700;

	public Renderer renderer;

	public Paddle jogador1;

	public Paddle jogador2;

	public Bola bola;

	public boolean bot = false, selecionarDificuldade;

	public boolean w, s, up, down;

	public int gameStatus = 0, scoreLimit = 2, jogadorVencedor; //0 = Menu, 1 = Pausado, 2 = Jogo, 3 = Fim

	public int botDifficulty, botMoves, botCooldown = 0;

	public Random random;

	public JFrame jframe;

	public Pong()
	{
		Timer timer = new Timer(20, this);
		random = new Random();

		jframe = new JFrame("Pong");

		renderer = new Renderer();

		jframe.setSize(width + 15, height + 35);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(renderer);
		jframe.addKeyListener(this);

		timer.start();
	}

	public void start()
	{
		gameStatus = 2;
		jogador1 = new Paddle(this, 1);
		jogador2 = new Paddle(this, 2);
		bola = new Bola(this);
	}

	public void update()
	{
		if (jogador1.score >= scoreLimit)
		{
			jogadorVencedor = 1;
			gameStatus = 3;
		}

		if (jogador2.score >= scoreLimit)
		{
			gameStatus = 3;
			jogadorVencedor = 2;
		}

		if (w)
		{
			jogador1.move(true);
		}
		if (s)
		{
			jogador1.move(false);
		}

		if (!bot)
		{
			if (up)
			{
				jogador2.move(true);
			}
			if (down)
			{
				jogador2.move(false);
			}
		}
		else
		{
			if (botCooldown > 0)
			{
				botCooldown--;

				if (botCooldown == 0)
				{
					botMoves = 0;
				}
			}

			if (botMoves < 10)
			{
				if (jogador2.y + jogador2.height / 2 < bola.y)
				{
					jogador2.move(false);
					botMoves++;
				}

				if (jogador2.y + jogador2.height / 2 > bola.y)
				{
					jogador2.move(true);
					botMoves++;
				}

				if (botDifficulty == 0)
				{
					botCooldown = 20;
				}
				if (botDifficulty == 1)
				{
					botCooldown = 15;
				}
				if (botDifficulty == 2)
				{
					botCooldown = 10;
				}
			}
		}

		bola.update(jogador1, jogador2);
	}

	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (gameStatus == 0) //Tela Inicial
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString("PONG", width / 2 - 75, 50);

			if (!selecionarDificuldade)
			{
				g.setFont(new Font("Arial", 1, 30));

				g.drawString("Pressione Z para 1 Jogador ", width / 2 - 185, height / 2 - 25);
				g.drawString("Pressione X para 2 Jogadores", width / 2 - 205, height / 2 + 25);
				g.drawString("<< Limite de Pontos: " + scoreLimit + " >>", width / 2 - 170, height / 2 + 75);
			}

			if (selecionarDificuldade)
			{
				String string = botDifficulty == 0 ? "Fácil" : (botDifficulty == 1 ? "Médio" : "Difícil");
	
				g.setFont(new Font("Arial", 1, 30));
	
				g.drawString("<< Dificuldade: " + string + " >>", width / 2 - 180, height / 2 - 25);
				g.drawString("Aperte Espaço para Começar", width / 2 - 220, height / 2 + 25);
			}
		}
		
		if (gameStatus == 1) //Tela Pausada
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));
			g.drawString("Pausado", width / 2 - 103, height / 2 - 25);
		}

		if (gameStatus == 1 || gameStatus == 2) //Jogo
		{
			g.setColor(Color.WHITE);

			g.setStroke(new BasicStroke(5f));

			g.drawLine(width / 2, 0, width / 2, height);

			g.setStroke(new BasicStroke(2f));

			//g.drawOval(width / 2 - 150, height / 2 - 150, 300, 300);

			g.setFont(new Font("Arial", 1, 50));

			g.drawString(String.valueOf(jogador1.score), width / 2 - 90, 50);
			g.drawString(String.valueOf(jogador2.score), width / 2 + 65, 50);

			jogador1.render(g);
			jogador2.render(g);
			bola.render(g);
		}

		if (gameStatus == 3) //Tela de Vencedor
		{
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", 1, 50));

			g.drawString("PONG", width / 2 - 75, 50);

			if (bot && jogadorVencedor == 2)
			{
				g.drawString("Você Perdeu!", width / 2 - 170, 200);
			}
			else
			{
				g.drawString("Jogador " + jogadorVencedor + " Venceu!", width / 2 - 205, 200);
			}

			g.setFont(new Font("Arial", 1, 30));

			g.drawString("Espaço para Jogar Novamente", width / 2 - 215, height / 2 - 25);
			g.drawString("ESC para Retornar ao Menu", width / 2 - 180, height / 2 + 25);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (gameStatus == 2)
		{
			update();
		}

		renderer.repaint();
	}

	public static void main(String[] args)
	{
		pong = new Pong();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int id = e.getKeyCode();
		
		controleTecla(e, true);
		
		if (id == KeyEvent.VK_RIGHT)
		{
			if (selecionarDificuldade)
			{
				if (botDifficulty < 2)
				{
					botDifficulty++;
				}
				else
				{
					botDifficulty = 0;
				}
			}
			else if (gameStatus == 0)
			{
				scoreLimit++;
			}
		}
		else if (id == KeyEvent.VK_LEFT)
		{
			if (selecionarDificuldade)
			{
				if (botDifficulty > 0)
				{
					botDifficulty--;
				}
				else
				{
					botDifficulty = 2;
				}
			}
			else if (gameStatus == 0 && scoreLimit > 1)
			{
				scoreLimit--;
			}
		}
		else if (id == KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3))
		{
			gameStatus = 0;
		}
		else if (id == KeyEvent.VK_Z && gameStatus == 0)
		{
			bot = true;
			selecionarDificuldade = true;
		}
		else if (id == KeyEvent.VK_X)
		{
			if ((gameStatus == 0) && (!selecionarDificuldade))//Se estiver na tela inicial
			{
					bot = false;
					start();
			}
		}

		else if (id == KeyEvent.VK_SPACE)
		{
			if (gameStatus == 3)//Se estiver na tela de vencedor
			{
				if (!selecionarDificuldade)
				{
					bot = false;
				}
				else
				{
					selecionarDificuldade = false;
				}

				start();
			}
			else if (gameStatus == 1)
			{
				gameStatus = 2;
			}
			else if (gameStatus == 2)
			{
				gameStatus = 1;
			}
			
			if ((gameStatus == 0) && (selecionarDificuldade))
			{
					//selecionarDificuldade = false;
					start();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		controleTecla(e, false);
	}
	
	private void controleTecla(KeyEvent event, boolean controle)
	{
		switch (event.getKeyCode()) 
		{
			case KeyEvent.VK_W:
				w = controle;
				break;
			case KeyEvent.VK_S:
				s = controle;
				break;
			case KeyEvent.VK_UP:
				up = controle;
				break;
			case KeyEvent.VK_DOWN:
				down = controle;
				break;
			default:
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}
}
