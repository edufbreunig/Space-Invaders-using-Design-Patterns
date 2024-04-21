1. Code is run using gradle clean build run
2. Implemented: Difficulty Level, Time and Score, and Undo and Cheat in the extension
3. Design Patterns classes:
    Singleton: DifficultyManager(Singleton), GameWindow(SingletonClient)
    Observer: Observer(Observer), ScoreBoard(ConcreteObserver), Subject(Subject), GameEgine(ConcreteSubject)
    Memento: Memento(Memento), Caretaker(Caretaker) , GameEngine(Originator)
4. Instructions:
    Undo: Game is saved when the player shoots, it can be undone by pressing key 'U'
    Cheat delete slow projectiles: press key '1'
    Cheat delete fast projectiles: press key '2'
    Cheat delete slow enemies: press '3'
    Cheat delete fast enemies: press '4'
    Difficulty level: select difficulty level in the beginning og the game or during the game by pressing buttons on the
   window.
5. All necessary information is mentioned above
