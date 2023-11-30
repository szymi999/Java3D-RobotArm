# Java3D-RobotArm
Ramię robota z 6. stopniami swobody zaprogramowane w języku Java z wykorzystaniem api Java3D

<b>Miałem do wykonania manipulator z sześcioma stopniami swobody.</b>
Miał przypominać ramię pokazane na rysunku poniżej
<br>
![image](https://github.com/szymi999/Java3D-RobotArm/assets/52047025/3302995a-fff8-499f-93b7-076e5b104060)

W projekcie stworzyłem świat, na środku którego jest hala a w niej zaprojektowany manipulator. Obok leży kula, którą ramię może podnieść i przenieść. Wszystko wygląda następująco:
<br>
![image](https://github.com/szymi999/Java3D-RobotArm/assets/52047025/56f1e2c7-7f01-4098-95e1-7fda7065da18)

<b>Co znajduje się w programie</b>
1. Rzeczy widoczne na zdjęciu
- otoczenie, w którym znajduje się robot
- przycisk „Chwyć obiekt” pozwalający złapać kulkę gdy chwytak naszego manipulatora dotyka ją. BranchGroup z kulką usuwa ze sceny i dodaje do naszego robota
- przycisk „Puść obiekt”, sprawiający że robot puszcza kulkę (jeśli ją trzymał). BranchGroup z kulką usuwa z naszego robota i dodaje do sceny
- przyciski „Zacznij nagrywać” i „Przestań nagrywać” odpowiadające za nagrywanie ruchu manipulatora. Po wciśnięciu pierwszego w klasie GameLoop z częstotliwością 60 razy na sekundę zapisuje wartość każdego kąta do tablicy. Po wciśnięciu drugiego przycisku kończy zapisywać kąty. Podczas nagrywania możemy również złapać, przenieść i puścić kulkę
- przyciski „Odtwórz nagranie” i „Zatrzymaj odtwarzanie” odpowiadające za odtwarzanie ruchu. Odtwarzanie ruchów też odbywa się w klasie GameLoop. Po wciśnięciu pierwszego najpierw robot ustawia się do początkowej pozycji, którą miał na początku nagrywania a potem odtwarza wszystkie ustawienia kątów, które zostały nagrane. Jeśli podczas nagrywania mieliśmy interakcję z kulką, to podczas odtwarzania manipulator będzie chciał to odtworzyć, ale jeśli kulki już nie będzie w tym
miejscu to w konsoli zostanie wypisane, że nie było obiektu do złapania
- przycisk „Zresetuj ustawienie kamery” ustawiający początkową pozycję kamery, czyli taką jaka jest pokazana na zdjęciu wyżej
2. Rzeczy, których nie widać:
- zmienione sterowanie (pierw używałem interfejsu KeyListener), usunąłem go i stworzyłem nowe klasy dziedziczące po Behavior – Moving i GameLoop. Pierwsza obsługuje klawiaturę i do tablicy zmiennych boolowskich (elementy tej tablicy odpowiadają danym klawiszom) wpisuje wartość true jeśli klawisz jest naciśnięty lub false gdy klawisz zostaje puszczony i nie jest naciśnięty. A druga klasa
– GameLoop odświeża się 60 razy na sekundę i odpowiada za wykonywanie tych ruchów.
- Program zawiera detekcję kolizji, czyli klasę CollisionDetector dziedziczącą po Behavior. Wykrywa ona kolizję z kulką lub z podłożem. Gdy jest kolizja to blokuje ona ruch manipulatora w kierunku, w którym ostatnio się poruszał

<b>Struktura programu:</b>
<br>
![image](https://github.com/szymi999/Java3D-RobotArm/assets/52047025/eacacbda-15db-428a-8a5f-89fe4a98b2d4)
