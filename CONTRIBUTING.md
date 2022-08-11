# How to Contribute to Eclipse Kanto - Yocto layer

First of all, thanks for considering to contribute to Eclipse Kanto - Yocto layer. We really
appreciate the time and effort you want to spend helping to improve things around here.

In order to get you started as fast as possible we need to go through some organizational issues first, though.

## Eclipse Contributor Agreement

Before your contribution can be accepted by the project team contributors must
electronically sign the Eclipse Contributor Agreement (ECA).

* http://www.eclipse.org/legal/ECA.php

Commits that are provided by non-committers must have a Signed-off-by field in
the footer indicating that the author is aware of the terms by which the
contribution has been provided to the project. The non-committer must
additionally have an Eclipse Foundation account and must have a signed Eclipse
Contributor Agreement (ECA) on file.

For more information, please see the Eclipse Committer Handbook:
https://www.eclipse.org/projects/handbook/#resources-commit

## Making Your Changes

* Fork the repository on GitHub.
* Create a new branch for your changes.
* Make your changes.
* Make sure you include test cases for non-trivial features.
* Make sure test cases provide sufficient code coverage (see GitHub actions for minimal accepted coverage).
* Make sure the test suite passes after your changes.
* Commit your changes into that branch.
* Use descriptive and meaningful commit messages. Start the first line of the commit message with the issue number and titile e.g. `[#9865] Add token based authentication`.
* Squash multiple commits that are related to each other semantically into a single one.
* Make sure you use the `-s` flag when committing as explained above.
* Push your changes to your branch in your forked repository.

## Submitting the Changes

Submit a pull request via the normal GitHub UI.

## After Submitting

* Do not use your branch for any other development, otherwise further changes that you make will be visible in the PR.
