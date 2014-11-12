# -*- coding: utf-8 -*-

# Takes an XML file that represents a Tulip project file
# and generates nice looking LaTeX tables from the use
# cases which are then written into a .tex file

import xml.etree.ElementTree as ElementTree
import HTMLParser

FILENAME = 'TourneyUseCases.tulip'
actor_dict = {}

def unescape_to_file(text, file):
	# Unescape twice to represent umlauts correctly
	text = parser.unescape(text)
	text = parser.unescape(text)

	file.write(text.encode('utf8'))

def escape_to_file(text, file):
	file.write(text.encode('utf8'))

def write_simple_item(item_name, item_value, file):
	escape_to_file('\n\t', file)
	escape_to_file(r'\hline', file)
	escape_to_file('\n\t', file)
	escape_to_file(r'\textbf{', file)
	unescape_to_file(item_name, file)
	escape_to_file(r'} & ', file)
	unescape_to_file(item_value, file)
	escape_to_file(r' \\', file)

def write_seperated_list_item(item_name, item_value_list, file):
	escape_to_file('\n\t', file)
	escape_to_file(r'\hline', file)
	escape_to_file('\n\t', file)
	escape_to_file(r'\textbf{', file)
	unescape_to_file(item_name, file)
	escape_to_file(r'} & ', file)
	unescape_to_file(", ".join(item_value_list), file)
	escape_to_file(r' \\', file)

def write_scenario_to_file(usecase, scenario_name, scenario, file):
	escape_to_file('\n\t', file)
	escape_to_file(r'\multicolumn{2}{| c |}{\textbf{', file)
	unescape_to_file(scenario_name, file)
	escape_to_file(r'}} \\', file)
	preconditions = []
	for precondition in scenario.find('preConditions').iter('preCondition'):
		preconditions.append(precondition.find('description').text)
	if precondition.find('description').text is not None:
		write_seperated_list_item('Vorbedingung', preconditions, file)
	else:
		write_seperated_list_item('Vorbedingung', '', file)
	escape_to_file('\n\t', file)
	escape_to_file(r'\hline', file)
	escape_to_file('\n\t', file)
	escape_to_file(r'\textbf{Ablauf} &', file)
	escape_to_file('\n\t\t', file)
	escape_to_file(r'\begin{enumerate}', file)
	writtenStep = 0
	for step in scenario.find('steps').iter('step'):
		writtenStep = 1
		escape_to_file('\n\t\t\t', file)
		escape_to_file(r'\item[', file)
		unescape_to_file(step.find('stepNumber').text + ".", file)
		escape_to_file(r'] ', file)
		if step.find('actorId').text is not None:
			escape_to_file(actor_dict[int(step.find('actorId').text)] + ': ', file)
		else:
			escape_to_file('System: ', file)
		if step.find('description').text is not None:
			unescape_to_file(step.find('description').text.strip(), file)
		if usecase.find('extensions') is not None:
			for extension_scenario in usecase.find('extensions').iter('extensionScenario'):
				if extension_scenario.find('associatedStep').text == step.find('stepNumber').text:
					escape_to_file('\n\t\t\t', file)
					escape_to_file(r'\newline', file)
					escape_to_file('\n\t\t\t', file)
					unescape_to_file('Sonderfall f&uuml;r Alternativablauf '
						+ extension_scenario.find('name').text + ': ', file)
					preconditions = []
					if extension_scenario.find('preConditions') is not None:
						for precondition in extension_scenario.find('preConditions').iter('preCondition'):
							preconditions.append(precondition.find('description').text)
						unescape_to_file(", ".join(preconditions), file)
	if writtenStep == 0:
		escape_to_file('\n\t\t\t', file)
		escape_to_file(r'\item', file)
	escape_to_file('\n\t\t', file)
	escape_to_file(r'\end{enumerate}', file)
	escape_to_file('\n\t', file)
	escape_to_file(r'\\', file)
	postconditions = []
	for postcondition in scenario.find('postConditions').iter('postCondition'):
		postconditions.append(postcondition.find('description').text)
	if postcondition.find('description').text is not None:
		write_seperated_list_item('Nachbedingung', postconditions, file)
	else:
		write_seperated_list_item('Nachbedingung', '', file)
	escape_to_file('\n\t', file)
	escape_to_file(r'\hline', file)

# Open the XML file
parser = HTMLParser.HTMLParser()
file = open(FILENAME + ".tex", 'w')
tree = ElementTree.parse(FILENAME)
root = tree.getroot()

# Write actor ids to the dictionary
for actor in root.find('actorsFolder').iter('actor'):
	actor_dict[int(actor.find('id').text)] = actor.find('name').text

# This is were the magic happens
for usecase in root.find('useCasesFolder').iter('useCase'):
	# Start the table
	escape_to_file(r'\begin{tabularx}{\textwidth}{| p{0.2\textwidth} | p{0.7415\textwidth} |}', file)

	# Write the first attributes to the table
	write_simple_item('Ziel', str(usecase.find('goal').text[26:-5]).strip(), file)
	actors = []
	for actor in usecase.find('actors').iter('actorId'):
		actors.append(actor_dict[int(actor.text)])
	write_seperated_list_item('Akteure', actors, file)
	if usecase.find('description').text is not None:
		write_simple_item('Beschreibung', str(usecase.find('description').text[26:-5]).strip(), file)
	else:
		write_simple_item('Beschreibung', '', file)
	if usecase.find('level').text is not None:
		write_simple_item('Ebene', str(usecase.find('level').text).strip(), file)
	if usecase.find('priority').text is not None:
		write_simple_item('Priorit&auml;t', str(usecase.find('priority').text).strip(), file)
	escape_to_file('\n\t', file)
	escape_to_file(r'\hline', file)

	# Write the main scenario
	write_scenario_to_file(usecase, 'Normalablauf', usecase.find('mainScenario'), file)

	# Print all available extension scenarios
	for extension_scenario in usecase.find('extensions').iter('extensionScenario'):
		write_scenario_to_file(usecase, 'Alternativablauf ' + extension_scenario.find('name').text,
			extension_scenario, file)

	escape_to_file('\n', file)

	# Close the table
	escape_to_file(r'\end{tabularx}', file)
	escape_to_file('\n\n', file)

#\textbf{Vorbedingung} & ... \\
#\hline
#\textbf{Ablauf} &
#	\begin{enumerate}
#		\item Nutzer: ...
#		\item Nutzer: ...\newline
#		Sonderfall für Alternativablauf 3a: asdf
#	\end{enumerate}
#\\
#\hline
#\textbf{Nachbedingung} & ... \\
#\hline