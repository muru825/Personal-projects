# Gitlet Design Document

Vivian Tran:

## Classes and Data Structures

### Class 1: Main

#### Fields

1. constructor: house all the methods needed (8),
2. Field 2: N/A


### Class 2: Repository
Summary: The repository is used in order to track our commits and our "git push origin main" it will hold all of our code AND files AND the history of each blob/ commit  does at a high level.
- handle data storage and retrieval
#### Fields

1. branches - maps the branch names to the commited blobs through the SHA-1 identifier
2. headcommit - tracks which file was the most recently added
3. staging area - area where "committed" files are placed into

### Class 3: CommitTree

#### Fields

1. SHA-1 identifier - stored as the file's key within a hashmap and is based on the timestamp
2. Parent -  key of wanted Blob 
3. message - git commit -m "quote" holds the message
4. timestamp - when the file was committed
5. right blob- the top blob if the tree breaks 
6. left blob - the bottom blov if the tree breaks


### Class 4: Blob

#### Fields

1. SHA-1 identifier - the key/ name of the item
2. content: what is in the file
3. File name // Hello.txt
4. boolean if the file was changed

## Algorithms

## Persistence

### Class 5: Stagging Area

#### Fields

    