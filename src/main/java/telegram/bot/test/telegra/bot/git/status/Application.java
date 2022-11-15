package telegram.bot.test.telegra.bot.git.status;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws IOException, InvalidRemoteException, TransportException, GitAPIException{
		
		
			// Local directory on this machine where we will clone remote repo.
			File localRepoDir = new File("C:\\Users\\Public\\JGIT_TESTING\\test");

			// Monitor to get git command progress printed on java System.out console
			TextProgressMonitor consoleProgressMonitor = new TextProgressMonitor(new PrintWriter(System.out));

			/*
			 * Git clone remote repo into local directory.
			 * 
			 * Equivalent of --> $ git clone https://github.com/deepanshujain92/test_git.git
			 */
			System.out.println("\n>>> Cloning repository\n");
			Repository repo = Git.cloneRepository().setProgressMonitor(consoleProgressMonitor).setDirectory(localRepoDir)
					.setURI("https://github.com/deepanshujain92/test_git").call().getRepository();

			try (Git git = new Git(repo)) {
				/*
				 * Get list of all branches (including remote) & print
				 * 
				 * Equivalent of --> $ git branch -a
				 * 
				 */
				System.out.println("\n>>> Listing all branches\n");
				git.branchList().setListMode(ListMode.ALL).call().stream().forEach(r  -> System.out.println(r.getName()));

				// Find develop branch from remote repo.
				Optional<String> developBranch = git.branchList().setListMode(ListMode.REMOTE).call().stream()
						.map(r -> r.getName()).filter(n -> n.contains("develop")).findAny();

				/*
				 * If develop branch present then checkout.
				 * 
				 * Equivalent of --> $ git checkout -b local-develop /remotes/origin/develop
				 */
				if (developBranch.isPresent()) {
					System.out.println("\n>>> Checking out develop branch\n");
					git.checkout().setProgressMonitor(consoleProgressMonitor).setCreateBranch(true).setName("local-develop")
							.setStartPoint(developBranch.get()).call();
				}

//				// Modify one file & append a line
//				System.out.println("\n>>> Modifying test.txt\n");
//				File fileInDevelop = Arrays.stream(localRepoDir.listFiles())
//						.filter(f -> f.getName().contains("test.txt")).findFirst().get();
//				Files.asCharSink(fileInDevelop, Charset.defaultCharset(), FileWriteMode.APPEND).write("Added by Program");

				/*
				 * Check status of modified file.
				 * 
				 * Equivalent of --> $ git status
				 * 
				 */
				System.out.println("\n>>> Printing status of local repository\n");
				Status status = git.status().setProgressMonitor(consoleProgressMonitor).call();
				System.out.println("Modified file = " + status.getModified());

				
				SpringApplication.run(Application.class, args);

			}

		}
		

}
