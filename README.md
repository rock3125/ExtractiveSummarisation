## Extractive Summarisation

Extractive summarization of text - simple algorithm that scores texts based on position in story, numbers, proper nouns,
thematic relationships between title and sentences, cosine relationships between sentences, and themes based on frequencies.

## Build and test on Ubuntu 16.04 or 18.04
This Kotlin isn't platform specific, however it was built and tested using Ubuntu.
```
gradle clean build
```
This setups a distributable `jar` with executable script and all relevant libraries in `dist/`
```
cd dist
./summarize.sh resources/test/romeo_and_juliet.txt 10
```
output
```
Romeo and Juliet .
Shakespeare homepage | Romeo and Juliet | Entire play ACT I PROLOGUE Two households , both alike in dignity , In fair Verona , where we lay our scene , From ancient grudge break to new mutiny , Where civil blood makes civil hands unclean .
JULIET O Romeo , Romeo !
BENVOLIO Tybalt , here slain , whom Romeo 's hand did slay ; Romeo that spoke him fair , bade him bethink How nice the quarrel was , and urged withal Your high displeasure : all this uttered With gentle breath , calm look , knees humbly bow 'd , Could not take truce with the unruly spleen Of Tybalt deaf to peace , but that he tilts With piercing steel at bold Mercutio 's breast , Who all as hot , turns deadly point to point , And , with a martial scorn , with one hand beats Cold death aside , and with the other sends It back to Tybalt , whose dexterity , Retorts it : Romeo he cries aloud , ' Hold , friends !
ROMEO 'T is torture , and not mercy : heaven is here , Where Juliet lives ; and every cat and dog And little mouse , every unworthy thing , Live here in heaven and may look on her ; But Romeo may not : more validity , More honourable state , more courtship lives In carrion-flies than Romeo : they my seize On the white wonder of dear Juliet 's hand And steal immortal blessing from her lips , Who even in pure and vestal modesty , Still blush , as thinking their own kisses sin ; But Romeo may not ; he is banished : Flies may do this , but I from this must fly : They are free men , but I am banished .
ROMEO Thou canst not speak of that thou dost not feel : Wert thou as young as I , Juliet thy love , An hour but married , Tybalt murdered , Doting like me and like me banished , Then mightst thou speak , then mightst thou tear thy hair , And fall upon the ground , as I do now , Taking the measure of an unmade grave .
thy Juliet is alive , For whose dear sake thou wast but lately dead ; There art thou happy : Tybalt would kill thee , But thou slew ' st Tybalt ; there are thou happy too : The law that threaten 'd death becomes thy friend And turns it to exile ; there art thou happy : A pack of blessings lights up upon thy back ; Happiness courts thee in her best array ; But , like a misbehaved and sullen wench , Thou pout ' st upon thy fortune and thy love : Take heed , take heed , for such die miserable .
FRIAR LAURENCE Hold , then ; go home , be merry , give consent To marry Paris : Wednesday is to-morrow : To-morrow night look that thou lie alone ; Let not thy nurse lie with thee in thy chamber : Take thou this vial , being then in bed , And this distilled liquor drink thou off ; When presently through all thy veins shall run A cold and drowsy humour , for no pulse Shall keep his native progress , but surcease : No warmth , no breath , shall testify thou livest ; The roses in thy lips and cheeks shall fade To paly ashes , thy eyes ' windows fall , Like death , when he shuts up the day of life ; Each part , deprived of supple government , Shall , stiff and stark and cold , appear like death : And in this borrow 'd likeness of shrunk death Thou shalt continue two and forty hours , And then awake as from a pleasant sleep .
Exit FRIAR LAURENCE Now must I to the monument alone ; Within three hours will fair Juliet wake : She will beshrew me much that Romeo Hath had no notice of these accidents ; But I will write again to Mantua , And keep her at my cell till Romeo come ; Poor living corse , closed in a dead man 's tomb !
Romeo , there dead , was husband to that Juliet ; And she , there dead , that Romeo 's faithful wife : I married them ; and their stol 'n marriage-day Was Tybalt 's dooms-day , whose untimely death Banish 'd the new-made bridegroom from the city , For whom , and not for Tybalt , Juliet pined .
```