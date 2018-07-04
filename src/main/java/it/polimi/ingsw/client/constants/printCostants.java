package it.polimi.ingsw.client.constants;

public class printCostants {
    public static final String CONNECTION_STRING = "Scegli la tua connessione";
    public static final String  CHOOSE1_CONNECTION_STRING = "1 ----> Socket";
    public static final String  CHOOSE2_CONNECTION_STRING = "2 ----> Rmi";
    public static final String CONNECTION_ERROR = "Errore di connessione con il server, riprovare";
    public static final String INSERT_USERNAME = "Inserisci il tuo username:" ;
    public static final String NICKNAME_TOO_LONG = "Errore, nickname troppo lungo";
    public static final String INSERT_SCHEMA_NAME = "Scrivi il nome dello schema che preferisci tra:";
    public static final String INFO_CUSTOM_SCHEMA = "\nOppure carica uno schema personalizzato(load)";
    public static final String CHOOSE_LOAD_CUSTOM_SCHEMA = "load";
    public static final String SCHEMA_ALREADY_INSERT = "Schema già inserito";
    public static final String INDEX_DICE_SPACE_ERROR = "Indice della riserva non corretto";
    public static final String ERROR_INSERT_DICE = "Errore nell'inserimento del dado";
    public static final String PRIVATE_OBJECT_INFO = "Il tuo obiettivo privato è il colore: ";
    public static final String CREATE_SCHEMA_INFO = "Hai scelto di creare la tua griglia: ";
    public static final String CHOOSE_GRID_NAME = "Scegli il nome della griglia:";
    public static final String INSERT_CONSTRAINT_INFO = "Inserisci la restrizione della cella:";
    public static final String CHOOSE_GRID_INFO = "La griglia che hai creato è questa:";
    public static final String MODIFY_GRID_INFO = "Desideri apportare modifiche? y si";
    public static final String SAVE_SCHEMA_INFO = "Vuoi salvare su file il tuo schema? y si";
    public static final String ERROR_SAVE_SCHEMA = "Errore con il salvataggio dello schema";
    public static final String COLOUR_COSTRAINT = "1) colore ";
    public static final String GREEN_INFO = "verde(g)-";
    public static final String RED_INFO = "rosso(r)-";
    public static final String BLUE_INFO = "blu(b)-";
    public static final String YELLOW_INFO = "giallo(y)";
    public static final String PURPLE_INFO = "-viola(p)";
    public static final String NUMBER_CONSTRAINT = "2) numero 1-6";
    public static final String EMPTY_CONSTRAINT = "3) nessuna restrizione (e)";
    public static final String ERROR_CONSTRAINT = " Restrizione già immessa nelle caselle adiacenti\nSelezionarne un'altra\n";
    public static final String INSERT_ROW = "Inserisci la riga che vuoi modificare";
    public static final String INSERT_COLUMN = "Inserisci la colonna che vuoi modificare";
    public static final String ROW_INFO = "Row";
    public static final String COLUMN_INFO = "Column";
    public static final String DONE_MODIFY = "Modifica avvenuta!";
    public static final String YOUR_SCHEME = "Questo è il tuo schema";
    public static final String MODIFY_GRID = "Vuoi modificare ancora la griglia? y si";
    public static final String FILE_INFO = "Il file ";
    public static final String ALREADY_EXIST = " esiste già";
    public static final String CREATE = " è stato creato";
    public static final String NOT_CREATE = " non può essere creato";
    public static final String SHOW_PRIVATE_OBJECT = "Mostra l'obiettivo privato";
    public static final String  SHOW_PUBLIC_OBJECT = "Mostra gli obiettivi pubblici";
    public static final String SHOW_OPPONENTS_SCHEMAS = "Mostra schemi avversari";
    public static final String SHOW_TOOL_CARD = "Mostra le carte utensili";
    public static final String SHOW_ROUND_TRACK = "Mostra il tracciato dei round";
    public static final String SHOW_SCHEME = "Mostra il tuo schema";
    public static final String INSERT_DICE = "InsertDice";
    public static final String INSERT_DICE_INFO = "Inserisci un dado";
    public static final String DRAFT_DICE = "DraftDice";
    public static final String DRAFT_DICE_INFO = "Prendi un dado dalla riserva";
    public static final String CANCEL_USE_TOOL_CARD = "CancelUseToolCard";
    public static final String CANCEL_USE_TOOL_CARD_INFO = "Annulla uso della carta utensile";
    public static final String USE_TOOL_CARD = "UseToolCard";
    public static final String USE_TOOL_CARD_INFO =  "Utilizza la carta utensile";
    public static final String END_TURN = "EndTurn";
    public static final String END_TURN_INFO = "Passa il turno";
    public static final String ROLL_DICE ="RollDice";
    public static final String ROLL_DICE_INFO = "Tira il dado";
    public static final String PLACE_DICE = "PlaceDice";
    public static final String PLACE_DICE_INFO = "Piazza il dado";
    public static final String ROLL_DICE_SPACE = "RollDiceSpace";
    public static final String ROLL_DICE_SPACE_INFO = "Tira i dadi della riserva";
    public static final String MOVE_DICE ="MoveDice";
    public static final String MOVE_DICE_INFO = "Muovi il dado";
    public static final String FLIP_DICE ="FlipDice";
    public static final String FLIP_DICE_INFO = "Gira il dado sulla faccia opposta";
    public static final String SWAP_DICE ="SwapDice";
    public static final String SWAP_DICE_INFO ="Scambia il dado con un dado nel tracciato dei round";
    public static final String PLACE_DICE_SPACE = "PlaceDiceSpace";
    public static final String PLACE_DICE_SPACE_INFO = "Lascia il dado nella riserva";
    public static final String SWAP_DICE_BAG = "SwapDiceBag";
    public static final String SWAP_DICE_BAG_INFO = "Pesca un dado dalla borsa dei dadi";
    public static final String CHANGE_VALUE ="ChangeValue";
    public static final String CHANGE_VALUE_INFO ="Aumenta/Diminuisci il valore del dado di 1";
    public static final String PUBLIC_OBJECTIVE_GAME = "Gli obiettivi pubblici della partita sono:";
    public static final String TOOL_CARD_GAME = "Le carte utensili sono:";
    public static final String TOOL_1_INFO = "1)Dopo aver scelto un dado,aumenta o diminuisci il valore del dado scelto di 1";
    public static final String TOOL_2_INFO = "2)Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore";
    public static final String TOOL_3_INFO = "3)Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore";
    public static final String TOOL_4_INFO = "4)Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento";
    public static final String TOOL_5_INFO = "5)Dopo aver scelto un dado,scambia quel dado con un dado sul tracciato dei round";
    public static final String TOOL_6_INFO = "6)Dopo aver scelto un dado, tira nuovamente quel dado";
    public static final String TOOL_7_INFO = "7)Tira nuovamente tutti i dadi della riserva";
    public static final String TOOL_8_INFO = "8)Dopo il tuo primo turno scegli immediatamente un altro dado";
    public static final String TOOL_9_INFO  = "9)Dopo aver scelto un dado, piazzalo in una casella che non sia adiacente a un altro dado";
    public static final String TOOL_10_INFO = "10)Dopo aver scelto un dado, giralo sulla faccia opposta";
    public static final String TOOL_11_INFO = "11)Dopo aver scelto un dado, riponilo nel sacchetto, poi pescane uno dal sacchetto";
    public static final String TOOL_12_INFO = "12)Muovi fino a due dadi dello stesso colore di un solo dado sul tracciato dei round";
    public static final String ERROR_TOOL_CARD = "Errore con la visualizzazione della carta utensile";
    public static final String CHOOSE_MOVES = "Scegli una tra le seguenti opzioni:";
    public static final String EMPTY_ROUND_TRACK =  "Tracciato dei round vuoto";
    public static final String START_SCENE = "Prima di cominciare...";
    public static final String START_SCENE_1 = "Desideri:";
    public static final String START_SCENE_BUILD_SCHEME = "1)Costruire il tuo schema";
    public static final String START_SCENE_STANDARD_SCHEME = "2)Utilizzare schemi predefiniti";
    public static final String INVALID_CHOOSE = "Scelta non valida";
    public static final String CHOOSE_LOAD_SCHEME = "Scegli il nome dello schema da caricare";
    public static final String EMPTY_CUSTOM_SCHEMAS = " Nessuno schema da caricare, te ne verrà assegnato uno allo scadere del tempo ";
    public static final String LOAD_THIS_SCHEME = "Hai caricato questo schema";
    public static final String ERROR_LOAD_SCHEME = "Errore con lo schema da caricare,te ne verrà assegnato uno allo scadere del tempo";
    public static final String WELCOME = "Welcome";
    public static final String GAME_EARLY_START = "La partita inizierà a breve";
    public static final String WAIT_PLAYERS = "Aspettando altri giocatori...";
    public static final String LOGIN_ERROR_USERNAME = "Login_error-username";
    public static final String NICKNAME_ALREADY_USE = "Errore, nickname già in uso";
    public static final String LOGIN_ERROR_GAME = "Login_error-game";
    public static final String ERROR_GAME_ALREADY_START = "Errore, partita già in corso";
    public static final String TRY_LATER = "Riprovare più tardi";
    public static final String PLAYER_ADD_IN_LOBBY = " si è aggiunto alla lobby\n";
    public static final String NUMBER_PLAYER_CONNECT = "Numero di giocatori  connessi:";
    public static final String PLAYER_DISCONNECTED = " si è disconnesso\n";
    public static final String PLAYER_ALREADY_CONNECT = "Numero di giocatori rimasti connessi:";
    public static final String CREATE_GAME = "\nPartita creata\n";
    public static final String APPROVED_SCHEME = "schema approvato: ";
    public static final String WAITING_CHOOSE_PLAYER = "Aspettando la scelta degli altri giocatori...\n";
    public static final String TIMER_SCHEME_ELAPSED = "Tempo scaduto! Schema scelto dal server!";
    public static final String PRESS_ENTER = "Premi invio!";
    public static final String ROUND = "Round";
    public static final String YOUR_SCHEME2 = "\nIl tuo schema:";
    public static final String TURN_START = "Turno iniziato, tocca a: ";
    public static final String IS_YOUR_TURN = "Tocca a te!";
    public static final String CHOOSE_VALUE_INFO = "ChooseValue";
    public static final String INSERT_NUMBER = "Inserisci un numero";
    public static final String ROLL_THIS_DICES ="Rolliamo 'sti dadi";
    public static final String REALLY_ROLL = "Vuoi davvero rollare il dado?";
    public static final String IS_RHETORICAL = "Era retorico.";
    public static final String ROLL_THIS_DIE = "Rolliamo sto dado";
    public static final String INCREMENT_DECREMENT_DICE = "Vuoi incrementare(I) o decrementare(D) il dado?";
    public static final String INCREMENT = "Increment";
    public static final String DECREMENT = "Decrement";
    public static final String INCORRECT_PARAMETER ="Parametro non corretto";
    public static final String INSERT_ROW_DICE = "Inserisci la riga in cui inserire il dado";
    public static final String INSERT_COLUMN_DICE ="Inserisci la colonna in cui inserire il dado";
    public static final String INVALID_INPUT = "Inserimento non valido";
    public static final String INDEX_DICE_SPACE ="Inserisci l'indice del dado della riserva:(Da 1 a ";
    public static final String INSERT_ROW2 ="Inserisci la riga";
    public static final String INSERT_COLUMN2 = "Inserisci la colonna";
    public static final String INVALID_FORMAT=     "Formato non valido";
    public static final String CHOOSE_TOOL_CARD = "Scegli la tool card da utilizzare:";
    public static final String CARD_USED = "Carta utilizzata! Favori rimanenti";
    public static final String FAVOR_ERROR ="Non hai abbastanza favori oppure non è permesso l'azione svolta dalla toolCard";
    public static final String CHANGE_VALUE_ACCEPTED = "Cambiamento valore accettato ";
    public static final String ERROR_CHANGE_VALUE ="Errore di cambiamento del valore";
    public static final String CORRECT_INSERT_DICE ="Dado inserito correttamente";
    public static final String VALUE_DICE = "Valore del dado ";
    public static final String ERROR_DICE_NOT_FOUND = "Errore,dado non trovato";
    public static final String DICE_EXCHANGE_CORRECT = "Dado scambiato correttamente";
    public static final String UNDO_ACTION = "Azione annullata,favori rimanenti ";
    public static final String FLIP_DICE_CORRECT = "Dado flippato";
    public static final String ROLL_DICE_CORRECT = "Abbiamo rollato 'sti dadi";
    public static final String VALUE_NEW_DICE ="valore nuovo dado: ";
    public static final String VALUE_EXCHANGE_DICE = "valore del dado cambiato: ";
    public static final String ERROR_EXCHANGE_DICE = "Errore nel cambiamento del valore del dado";
    public static final String INDEX_ROUND_TRACK = "Inserisci il numero del round da cui prendere il dado";
    public static final String INDEX_DICE_ROUND_TRACK ="Inserisci il numero del dado che vuoi prendere dalla RoundTrack";
    public static final String INVALID_PARAMETER = "Parametro non valido";
    public static final String INVALID_INDEX = "Indice non valido";
    public static final String INDEX_ROW_FROM = "Inserisci l'indice della riga da cui prendere il dado:";
    public static final String INDEX_COLUMN_FROM = "Inserisci l'indice della colonna da cui prendere il dado:";
    public static final String INDEX_ROW_TO = "Inserisci l'indice della riga in cui spostare il dado:";
    public static final String INDEX_COLUMN_TO = "Inserisci l'indice della colonna in cui spostare il dado:";
    public static final String ACTION_CORRECT= "Azione accettata";
    public static final String MOVE_DICE_CORRECT = "Dado spostato correttamente";
    public static final String REQUIREMENT_NOT_RESPECT = "Requisiti dello spostamento del dado non rispettati";
    public static final String FLIP_THIS_DICE = "Flippa 'sto dado";
    public static final String SWAP_THIS_DICE ="Lo swappo tutto 'sto dado";
    public static final String CHOOSE_DICE_VALUE = "Scegli il valore dal dado: ";
    public static final String SCHEMA_APPROVED = "schema approvato: ";
    public static final String YOU_WIN = "haiVinto";
    public static final String YOU_LOSE = "haiPerso";
    public static final String RANKING = "Classifica: ";
    public static final String RANKING_TITLE = "Pos    Giocatore     Punteggio";
    public static final String CONNECTION = "connection";
    public static final String LOGIN ="login";
    public static final String CHOOSE_GRAPHIC_INTERFACE = "Scegli l'interfaccia grafica";
    public static final String CLI_CHOOSE ="1 ----> Cli";
    public static final String GUI_CHOOSE = "2-----> GUI";
    public static final String START_GAME ="startGame";
    public static final String RESTART_GAME = "Vuoi giocare una nuova partita?(y)";
    public static final String CLOSE_GAME ="Chiusura del gioco";
    public static final String SERVER_CONNECTION_ERROR = "Errore di collegamento con il server";
    public static final String THREAD_ERROR = "Errore di sospensione del thread";

}
