package de.uniks.liverisk.model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Game 
{

   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return name;
   }

   public Game setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   public static final java.util.ArrayList<Platform> EMPTY_platforms = new java.util.ArrayList<Platform>()
   { @Override public boolean add(Platform value){ throw new UnsupportedOperationException("No direct add! Use xy.withPlatforms(obj)"); }};


   public static final String PROPERTY_platforms = "platforms";

   private java.util.ArrayList<Platform> platforms = null;

   public java.util.ArrayList<Platform> getPlatforms()
   {
      if (this.platforms == null)
      {
         return EMPTY_platforms;
      }

      return this.platforms;
   }

   public Game withPlatforms(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withPlatforms(i);
            }
         }
         else if (item instanceof Platform)
         {
            if (this.platforms == null)
            {
               this.platforms = new java.util.ArrayList<Platform>();
            }
            if ( ! this.platforms.contains(item))
            {
               this.platforms.add((Platform)item);
               ((Platform)item).setGame(this);
               firePropertyChange("platforms", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Game withoutPlatforms(Object... value)
   {
      if (this.platforms == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutPlatforms(i);
            }
         }
         else if (item instanceof Platform)
         {
            if (this.platforms.contains(item))
            {
               this.platforms.remove((Platform)item);
               ((Platform)item).setGame(null);
               firePropertyChange("platforms", item, null);
            }
         }
      }
      return this;
   }


   public static final String PROPERTY_selectedPlatform = "selectedPlatform";

   private Platform selectedPlatform = null;

   public Platform getSelectedPlatform()
   {
      return this.selectedPlatform;
   }

   public Game setSelectedPlatform(Platform value)
   {
      if (this.selectedPlatform != value)
      {
         Platform oldValue = this.selectedPlatform;
         if (this.selectedPlatform != null)
         {
            this.selectedPlatform = null;
            oldValue.setSelectedBy(null);
         }
         this.selectedPlatform = value;
         if (value != null)
         {
            value.setSelectedBy(this);
         }
         firePropertyChange("selectedPlatform", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<Player> EMPTY_players = new java.util.ArrayList<Player>()
   { @Override public boolean add(Player value){ throw new UnsupportedOperationException("No direct add! Use xy.withPlayers(obj)"); }};


   public static final String PROPERTY_players = "players";

   private java.util.ArrayList<Player> players = null;

   public java.util.ArrayList<Player> getPlayers()
   {
      if (this.players == null)
      {
         return EMPTY_players;
      }

      return this.players;
   }

   public Game withPlayers(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withPlayers(i);
            }
         }
         else if (item instanceof Player)
         {
            if (this.players == null)
            {
               this.players = new java.util.ArrayList<Player>();
            }
            if ( ! this.players.contains(item))
            {
               this.players.add((Player)item);
               ((Player)item).setGame(this);
               firePropertyChange("players", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Game withoutPlayers(Object... value)
   {
      if (this.players == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutPlayers(i);
            }
         }
         else if (item instanceof Player)
         {
            if (this.players.contains(item))
            {
               this.players.remove((Player)item);
               ((Player)item).setGame(null);
               firePropertyChange("players", item, null);
            }
         }
      }
      return this;
   }


   public static final String PROPERTY_winner = "winner";

   private Player winner = null;

   public Player getWinner()
   {
      return this.winner;
   }

   public Game setWinner(Player value)
   {
      if (this.winner != value)
      {
         Player oldValue = this.winner;
         if (this.winner != null)
         {
            this.winner = null;
            oldValue.setGameWon(null);
         }
         this.winner = value;
         if (value != null)
         {
            value.setGameWon(this);
         }
         firePropertyChange("winner", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_currentPlayer = "currentPlayer";

   private Player currentPlayer = null;

   public Player getCurrentPlayer()
   {
      return this.currentPlayer;
   }

   public Game setCurrentPlayer(Player value)
   {
      if (this.currentPlayer != value)
      {
         Player oldValue = this.currentPlayer;
         if (this.currentPlayer != null)
         {
            this.currentPlayer = null;
            oldValue.setCurrentGame(null);
         }
         this.currentPlayer = value;
         if (value != null)
         {
            value.setCurrentGame(this);
         }
         firePropertyChange("currentPlayer", oldValue, value);
      }
      return this;
   }



   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getName());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setSelectedPlatform(null);
      this.setWinner(null);
      this.setCurrentPlayer(null);

      this.withoutPlatforms(this.getPlatforms().clone());


      this.withoutPlayers(this.getPlayers().clone());


   }


}